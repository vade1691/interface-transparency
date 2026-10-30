package com.interfacetransparency;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.ClientTick;
import net.runelite.api.gameval.InterfaceID.Chatbox;
import net.runelite.api.gameval.InterfaceID.ToplevelOsrsStretch;
import net.runelite.api.gameval.InterfaceID.ToplevelPreEoc;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Interface Transparency",
	description = "Change the opacity of the side panel tabs and the chat tabs",
	tags = {"opacity", "transparent", "tabs", "stones", "chat", "interface"}
)
public class InterfaceTransparencyPlugin extends Plugin
{
	// ---- Resizable modern (ToplevelPreEoc, group 164) ----
	// There is no stone/icon 10 in this layout's widget tree.
	private static final int[] MODERN_STONES = {
		ToplevelPreEoc.STONE0, ToplevelPreEoc.STONE1, ToplevelPreEoc.STONE2,
		ToplevelPreEoc.STONE3, ToplevelPreEoc.STONE4, ToplevelPreEoc.STONE5,
		ToplevelPreEoc.STONE6, ToplevelPreEoc.STONE7, ToplevelPreEoc.STONE8,
		ToplevelPreEoc.STONE9, ToplevelPreEoc.STONE11, ToplevelPreEoc.STONE12,
		ToplevelPreEoc.STONE13
	};

	private static final int[] MODERN_ICONS = {
		ToplevelPreEoc.ICON0, ToplevelPreEoc.ICON1, ToplevelPreEoc.ICON2,
		ToplevelPreEoc.ICON3, ToplevelPreEoc.ICON4, ToplevelPreEoc.ICON5,
		ToplevelPreEoc.ICON6, ToplevelPreEoc.ICON7, ToplevelPreEoc.ICON8,
		ToplevelPreEoc.ICON9, ToplevelPreEoc.ICON11, ToplevelPreEoc.ICON12,
		ToplevelPreEoc.ICON13
	};

	private static final int[] MODERN_BACKGROUND = {
		ToplevelPreEoc.SIDE_STATIC_BACKGROUND,
		ToplevelPreEoc.SIDE_MOVABLE_BACKGROUND
	};

	// ---- Resizable classic (ToplevelOsrsStretch, group 161) ----
	// This layout has all 14 stones/icons (0-13).
	// TODO: SIDE_MENU_GRAPHIC0 and 4 (and any others) may exist; add them if parts of the panel don't fade.
	private static final int[] CLASSIC_STONES = {
		ToplevelOsrsStretch.STONE0, ToplevelOsrsStretch.STONE1, ToplevelOsrsStretch.STONE2,
		ToplevelOsrsStretch.STONE3, ToplevelOsrsStretch.STONE4, ToplevelOsrsStretch.STONE5,
		ToplevelOsrsStretch.STONE6, ToplevelOsrsStretch.STONE7, ToplevelOsrsStretch.STONE8,
		ToplevelOsrsStretch.STONE9, ToplevelOsrsStretch.STONE10, ToplevelOsrsStretch.STONE11,
		ToplevelOsrsStretch.STONE12, ToplevelOsrsStretch.STONE13
	};

	private static final int[] CLASSIC_ICONS = {
		ToplevelOsrsStretch.ICON0, ToplevelOsrsStretch.ICON1, ToplevelOsrsStretch.ICON2,
		ToplevelOsrsStretch.ICON3, ToplevelOsrsStretch.ICON4, ToplevelOsrsStretch.ICON5,
		ToplevelOsrsStretch.ICON6, ToplevelOsrsStretch.ICON7, ToplevelOsrsStretch.ICON8,
		ToplevelOsrsStretch.ICON9, ToplevelOsrsStretch.ICON10, ToplevelOsrsStretch.ICON11,
		ToplevelOsrsStretch.ICON12, ToplevelOsrsStretch.ICON13
	};

	// The side pillars (161.39 and 161.40)
	private static final int[] CLASSIC_PILLARS = {
		ToplevelOsrsStretch.SIDE_MENU_GRAPHIC1,
		ToplevelOsrsStretch.SIDE_MENU_GRAPHIC2
	};

	// Side graphics controlled by both the stones and the icons sliders (161.41 and 161.57).
	// A widget can only have one opacity, so the more transparent of the two settings is used.
	private static final int[] CLASSIC_SHARED = {
		ToplevelOsrsStretch.SIDE_MENU_GRAPHIC3,
		ToplevelOsrsStretch.SIDE_MENU_GRAPHIC5
	};

	// The panel background only (161.38)
	private static final int[] CLASSIC_BACKGROUND = {
		ToplevelOsrsStretch.SIDE_BACKGROUND
	};

	// ---- Chat tabs (Chatbox, group 162), shared by every layout ----
	// These widgets have child widgets (button backgrounds, etc.), so they are
	// faded together with all of their children.
	private static final int[] CHAT_BUTTONS = {
		Chatbox.CHAT_ALL,
		Chatbox.CHAT_GAME,
		Chatbox.CHAT_PUBLIC,
		Chatbox.CHAT_PRIVATE,
		Chatbox.CHAT_FRIENDSCHAT,
		Chatbox.CHAT_CLAN,
		Chatbox.CHAT_TRADE,
		Chatbox.REPORTABUSE
	};

	private static final int[] CHAT_BACKGROUND = {
		Chatbox.CONTROLS_BACKGROUND
	};

	// 0 means fully transparent. Raise this if you want a minimum visibility.
	private static final int MIN_PERCENT = 0;

	/**
	 * What we know about one widget we have changed.
	 * original: the opacity the game itself gave it (e.g. via the "Transparent side panel" or
	 *           "Transparent chatbox" settings).
	 * lastSet:  the last value this plugin wrote. If the widget's current opacity differs from
	 *           it, the game changed it, and the current value becomes the new original.
	 */
	private static class State
	{
		Widget widget;
		int original;
		int lastSet;
		boolean modified;
	}

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private InterfaceTransparencyConfig config;

	private final Map<Long, State> states = new HashMap<>();

	@Provides
	InterfaceTransparencyConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InterfaceTransparencyConfig.class);
	}

	@Override
	protected void shutDown()
	{
		clientThread.invoke(this::restoreAll);
	}

	@Subscribe
	public void onClientTick(ClientTick tick)
	{
		// Only the active layout's widgets exist, so applying both is harmless:
		// the other layout's lookups just return null.
		boolean modern = config.modernGrouped();
		apply(MODERN_STONES, modern ? config.modernOpacity() : config.modernStones(), false);
		apply(MODERN_ICONS, modern ? config.modernOpacity() : config.modernIcons(), false);
		apply(MODERN_BACKGROUND, modern ? config.modernOpacity() : config.modernBackground(), false);

		boolean classic = config.classicGrouped();
		apply(CLASSIC_STONES, classic ? config.classicOpacity() : config.classicStones(), false);
		apply(CLASSIC_ICONS, classic ? config.classicOpacity() : config.classicIcons(), false);
		apply(CLASSIC_SHARED, classic ? config.classicOpacity() : Math.min(config.classicStones(), config.classicIcons()), false);
		apply(CLASSIC_PILLARS, classic ? config.classicOpacity() : config.classicPillars(), false);
		apply(CLASSIC_BACKGROUND, classic ? config.classicOpacity() : config.classicBackground(), false);

		boolean chat = config.chatGrouped();
		apply(CHAT_BUTTONS, chat ? config.chatOpacity() : config.chatButtons(), true);
		apply(CHAT_BACKGROUND, chat ? config.chatOpacity() : config.chatBackground(), true);
	}

	private void apply(int[] ids, int percent, boolean includeChildren)
	{
		int clamped = Math.max(MIN_PERCENT, Math.min(100, percent));

		for (int id : ids)
		{
			Widget w = client.getWidget(id);
			if (includeChildren)
			{
				applyTree(w, percent, clamped);
			}
			else if (w != null)
			{
				applyWidget(w, percent, clamped);
			}
		}
	}

	private void applyTree(Widget w, int percent, int clamped)
	{
		if (w == null)
		{
			return;
		}

		applyWidget(w, percent, clamped);
		applyAll(w.getStaticChildren(), percent, clamped);
		applyAll(w.getDynamicChildren(), percent, clamped);
		applyAll(w.getNestedChildren(), percent, clamped);
	}

	private void applyAll(Widget[] widgets, int percent, int clamped)
	{
		if (widgets == null)
		{
			return;
		}

		for (Widget child : widgets)
		{
			applyTree(child, percent, clamped);
		}
	}

	/**
	 * Scales one widget's visibility by the slider percent, relative to whatever opacity the
	 * game itself gave it (RuneLite opacity: 0 = opaque, 255 = invisible). At 100% the widget
	 * is left at the game's value, so the in-game transparency settings keep working.
	 */
	private void applyWidget(Widget w, int percent, int clamped)
	{
		// Dynamic children share their parent's id and are told apart by index
		long key = ((long) w.getId() << 16) | (w.getIndex() + 1);

		State s = states.get(key);
		if (s == null)
		{
			if (percent >= 100)
			{
				return; // never touched this one, nothing to undo
			}
			s = new State();
			states.put(key, s);
		}

		s.widget = w;
		int current = w.getOpacity();
		if (!s.modified || current != s.lastSet)
		{
			// Not a value we wrote, so the game set it. Treat it as the new baseline.
			s.original = current;
		}

		int target = percent >= 100 ? s.original : 255 - ((255 - s.original) * clamped / 100);
		if (current != target)
		{
			w.setOpacity(target);
		}

		if (percent >= 100)
		{
			states.remove(key);
		}
		else
		{
			s.lastSet = target;
			s.modified = true;
		}
	}

	/**
	 * Puts back the game's own opacity for every widget this plugin changed.
	 */
	private void restoreAll()
	{
		for (State s : states.values())
		{
			if (s.modified && s.widget != null && s.widget.getOpacity() == s.lastSet)
			{
				s.widget.setOpacity(s.original);
			}
		}
		states.clear();
	}
}

package com.menutabtransparency;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.ClientTick;
import net.runelite.api.gameval.InterfaceID.ToplevelOsrsStretch;
import net.runelite.api.gameval.InterfaceID.ToplevelPreEoc;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Menu Tab Transparency",
	description = "Change the opacity of the side panel tabs, icons and background",
	tags = {"opacity", "transparent", "tabs", "stones", "interface"}
)
public class MenuTabTransparencyPlugin extends Plugin
{
	// ---- Resizable modern (ToplevelPreEoc, group 164) ----
	// There is no stone/icon 10 in this layout's widget tree.
	private static final int[] MODERN = {
		ToplevelPreEoc.STONE0, ToplevelPreEoc.STONE1, ToplevelPreEoc.STONE2,
		ToplevelPreEoc.STONE3, ToplevelPreEoc.STONE4, ToplevelPreEoc.STONE5,
		ToplevelPreEoc.STONE6, ToplevelPreEoc.STONE7, ToplevelPreEoc.STONE8,
		ToplevelPreEoc.STONE9, ToplevelPreEoc.STONE11, ToplevelPreEoc.STONE12,
		ToplevelPreEoc.STONE13,

		ToplevelPreEoc.ICON0, ToplevelPreEoc.ICON1, ToplevelPreEoc.ICON2,
		ToplevelPreEoc.ICON3, ToplevelPreEoc.ICON4, ToplevelPreEoc.ICON5,
		ToplevelPreEoc.ICON6, ToplevelPreEoc.ICON7, ToplevelPreEoc.ICON8,
		ToplevelPreEoc.ICON9, ToplevelPreEoc.ICON11, ToplevelPreEoc.ICON12,
		ToplevelPreEoc.ICON13,

		ToplevelPreEoc.SIDE_STATIC_BACKGROUND,
		ToplevelPreEoc.SIDE_MOVABLE_BACKGROUND
	};

	// ---- Resizable classic (ToplevelOsrsStretch, group 161) ----
	// This layout has all 14 stones/icons (0-13).
	// SIDE_BACKGROUND and the SIDE_MENU_GRAPHIC* widgets are the panel background/graphics.
	// TODO: SIDE_MENU_GRAPHIC0 and 4 (and any others) may exist; add them if parts of the panel don't fade.
	private static final int[] CLASSIC = {
		ToplevelOsrsStretch.STONE0, ToplevelOsrsStretch.STONE1, ToplevelOsrsStretch.STONE2,
		ToplevelOsrsStretch.STONE3, ToplevelOsrsStretch.STONE4, ToplevelOsrsStretch.STONE5,
		ToplevelOsrsStretch.STONE6, ToplevelOsrsStretch.STONE7, ToplevelOsrsStretch.STONE8,
		ToplevelOsrsStretch.STONE9, ToplevelOsrsStretch.STONE10, ToplevelOsrsStretch.STONE11,
		ToplevelOsrsStretch.STONE12, ToplevelOsrsStretch.STONE13,

		ToplevelOsrsStretch.ICON0, ToplevelOsrsStretch.ICON1, ToplevelOsrsStretch.ICON2,
		ToplevelOsrsStretch.ICON3, ToplevelOsrsStretch.ICON4, ToplevelOsrsStretch.ICON5,
		ToplevelOsrsStretch.ICON6, ToplevelOsrsStretch.ICON7, ToplevelOsrsStretch.ICON8,
		ToplevelOsrsStretch.ICON9, ToplevelOsrsStretch.ICON10, ToplevelOsrsStretch.ICON11,
		ToplevelOsrsStretch.ICON12, ToplevelOsrsStretch.ICON13,

		// Panel background and graphics (161.38 - 161.41, 161.57)
		ToplevelOsrsStretch.SIDE_BACKGROUND,
		ToplevelOsrsStretch.SIDE_MENU_GRAPHIC1,
		ToplevelOsrsStretch.SIDE_MENU_GRAPHIC2,
		ToplevelOsrsStretch.SIDE_MENU_GRAPHIC3,
		ToplevelOsrsStretch.SIDE_MENU_GRAPHIC5
	};

	// Don't let the tabs become impossible to find
	private static final int MIN_PERCENT = 10;

	/**
	 * What we know about one widget we have changed.
	 * original: the opacity the game itself gave it (e.g. via the "Transparent side panel"
	 *           setting).
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
	private MenuTabTransparencyConfig config;

	private final Map<Long, State> states = new HashMap<>();

	@Provides
	MenuTabTransparencyConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MenuTabTransparencyConfig.class);
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
		apply(MODERN, config.modernOpacity());
		apply(CLASSIC, config.classicOpacity());
	}

	private void apply(int[] ids, int percent)
	{
		int clamped = Math.max(MIN_PERCENT, Math.min(100, percent));

		for (int id : ids)
		{
			Widget w = client.getWidget(id);
			if (w != null)
			{
				applyWidget(w, percent, clamped);
			}
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

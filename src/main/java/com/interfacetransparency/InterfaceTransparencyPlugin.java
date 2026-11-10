package com.interfacetransparency;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InterfaceID.Chatbox;
import net.runelite.api.gameval.InterfaceID.Orbs;
import net.runelite.api.gameval.InterfaceID.ToplevelOsrsStretch;
import net.runelite.api.gameval.InterfaceID.ToplevelPreEoc;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Interface Transparency",
	description = "Change the opacity of the side panel tabs, the chat tabs and the minimap orbs and surround",
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

	// 162.2 and its children, except 162.3, which is handled on its own (see applyAbsolute)
	private static final int[] CHAT_BACKGROUND = {
		Chatbox.CONTROLS_BACKGROUND
	};

	// ---- Minimap ----
	// The orbs (Orbs, group 160) are shared by every layout. They have child widgets
	// (backing, icon, indicator and so on), so they are faded together with all of them.
	private static final int[] MINIMAP_ORBS = {
		Orbs.ORB_HEALTH,
		Orbs.ORB_PRAYER,
		Orbs.ORB_RUNENERGY,
		Orbs.ORB_SPECENERGY,
		Orbs.ORB_STORE,
		Orbs.ORB_WORLDMAP,
		Orbs.WIKI,
		Orbs.ORB_CONTENTRECOM,
		Orbs.XP_DROPS
	};

	// The boxes behind the orbs, including the dark "empty" disc each orb sits on.
	// These follow the Orb boxes slider and are faded linearly, not curved.
	private static final Set<Integer> ORB_BACKINGS = Set.of(
		Orbs.HEALTH_BACKING,
		Orbs.PRAYER_BACKING,
		Orbs.RUNENERGY_BACKING,
		Orbs.SPECENERGY_BACKING,
		Orbs.CR_BACKING,
		Orbs.WORLDMAP_BACKING,

		Orbs.ORB_HEALTH_EMPTY,
		Orbs.HEALTH_EMPTY_CONTENTS,
		Orbs.ORB_PRAYER_EMPTY,
		Orbs.ORB_PRAYER_EMPTY_GRAPHIC0,
		Orbs.ORB_RUNENERGY_EMPTY,
		Orbs.ORB_RUNENERGY_EMPTY_GRAPHIC0,
		Orbs.ORB_SPECENERGY_EMPTY,
		Orbs.ORB_SPECENERGY_EMPTY_GRAPHIC0
	);

	// The rest of each orb is several widgets stacked on top of each other, and each one is faded. Overlapping
	// layers keep the combined result looking opaque until each is quite transparent, so the orbs
	// slider is curved to compensate. Raise this if the orb icons still barely change at high values,
	// lower it (1 = no curve) if they change too fast.
	private static final int ORB_LAYERS = 2;

	// The minimap surround in the resizable modern layout.
	// Deliberately not included: ToplevelPreEoc.MINIMAP, which is the map itself.
	private static final int[] MINIMAP_SURROUND = {
		ToplevelPreEoc.MAP_NOCLICK_0, ToplevelPreEoc.MAP_NOCLICK_1, ToplevelPreEoc.MAP_NOCLICK_2,
		ToplevelPreEoc.MAP_NOCLICK_3, ToplevelPreEoc.MAP_NOCLICK_4, ToplevelPreEoc.MAP_NOCLICK_5,
		ToplevelPreEoc.MAP_MINIMAP_GRAPHIC6,
		ToplevelPreEoc.MAP_MINIMAP_GRAPHIC9
	};

	// The minimap surround in the resizable classic layout (161.32).
	// TODO: the classic equivalents of MAP_NOCLICK_0-5 and MAP_MINIMAP_GRAPHIC6 are not added yet.
	private static final int[] MINIMAP_SURROUND_CLASSIC = {
		ToplevelOsrsStretch.MAP_MINIMAP_GRAPHIC9
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

	// ---- "What's new" message ----
	// Shown once in the chat box on the first login after the plugin updates.
	// When you release something players should hear about, bump VERSION and edit WHATS_NEW.
	private static final String CONFIG_GROUP = "interfacetransparency";
	private static final String VERSION_KEY = "lastSeenVersion";
	private static final String VERSION = "2";
	private static final String WHATS_NEW =
		"Interface Transparency: new minimap orb and surround transparency. Open the plugin settings to try it.";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ConfigManager configManager;

	@Inject
	private InterfaceTransparencyConfig config;

	private final Map<Long, State> states = new HashMap<>();

	private boolean updateMessagePending;

	@Provides
	InterfaceTransparencyConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InterfaceTransparencyConfig.class);
	}

	@Override
	protected void startUp()
	{
		// If the plugin is switched on while already logged in, greet straight away
		updateMessagePending = client.getGameState() == GameState.LOGGED_IN && needsUpdateMessage();
	}

	@Override
	protected void shutDown()
	{
		clientThread.invoke(this::restoreAll);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN && needsUpdateMessage())
		{
			updateMessagePending = true;
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		// Wait a tick after login so the chat box is ready
		if (!updateMessagePending || client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		updateMessagePending = false;
		client.addChatMessage(
			ChatMessageType.GAMEMESSAGE,
			"",
			new ChatMessageBuilder().append(ChatColorType.HIGHLIGHT).append(WHATS_NEW).build(),
			null);
		configManager.setConfiguration(CONFIG_GROUP, VERSION_KEY, VERSION);
	}

	private boolean needsUpdateMessage()
	{
		return config.showUpdateMessage()
			&& !VERSION.equals(configManager.getConfiguration(CONFIG_GROUP, VERSION_KEY));
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
		applyDynamicChildren(ToplevelPreEoc.SIDE_CONTAINER, modern ? config.modernOpacity() : config.modernBorder());

		boolean classic = config.classicGrouped();
		apply(CLASSIC_STONES, classic ? config.classicOpacity() : config.classicStones(), false);
		apply(CLASSIC_ICONS, classic ? config.classicOpacity() : config.classicIcons(), false);
		apply(CLASSIC_SHARED, classic ? config.classicOpacity() : Math.min(config.classicStones(), config.classicIcons()), false);
		apply(CLASSIC_PILLARS, classic ? config.classicOpacity() : config.classicPillars(), false);
		apply(CLASSIC_BACKGROUND, classic ? config.classicOpacity() : config.classicBackground(), false);

		boolean chat = config.chatGrouped();
		apply(CHAT_BUTTONS, chat ? config.chatOpacity() : config.chatButtons(), true);
		apply(CHAT_BACKGROUND, chat ? config.chatOpacity() : config.chatBackground(), true);
		applyAbsolute(Chatbox.CONTROLS_BACKGROUND_GRAPHIC, chat ? config.chatOpacity() : config.chatBackground(),
			config.chatKeepBackgroundOpaque());

		boolean minimap = config.minimapGrouped();
		applyOrbs(
			minimap ? config.minimapOpacity() : config.minimapOrbs(),
			minimap ? config.minimapOpacity() : config.minimapOrbBoxes());
		int surround = minimap ? config.minimapOpacity() : config.minimapSurround();
		apply(MINIMAP_SURROUND, surround, false);
		apply(MINIMAP_SURROUND_CLASSIC, surround, false);
	}

	/**
	 * Converts the slider value into the per-layer opacity that gives the same overall look when
	 * ORB_LAYERS layers are stacked. 100 stays 100 so the game's own appearance is untouched.
	 */
	private static int layered(int percent)
	{
		if (percent >= 100)
		{
			return 100;
		}

		double visible = Math.max(0, percent) / 100.0;
		return (int) Math.round(100 * (1 - Math.pow(1 - visible, 1.0 / ORB_LAYERS)));
	}

	/**
	 * Fades every orb and all of its children. The backing boxes use their own slider value as it
	 * is; everything else uses the orbs slider with the layered curve.
	 */
	private void applyOrbs(int orbPercent, int boxPercent)
	{
		for (int id : MINIMAP_ORBS)
		{
			applyOrbTree(client.getWidget(id), orbPercent, boxPercent);
		}
	}

	private void applyOrbTree(Widget w, int orbPercent, int boxPercent)
	{
		if (w == null)
		{
			return;
		}

		int p = ORB_BACKINGS.contains(w.getId()) ? boxPercent : layered(orbPercent);
		applyWidget(w, p, Math.max(MIN_PERCENT, Math.min(100, p)));
		applyOrbChildren(w.getStaticChildren(), orbPercent, boxPercent);
		applyOrbChildren(w.getDynamicChildren(), orbPercent, boxPercent);
		applyOrbChildren(w.getNestedChildren(), orbPercent, boxPercent);
	}

	private void applyOrbChildren(Widget[] widgets, int orbPercent, int boxPercent)
	{
		if (widgets == null)
		{
			return;
		}

		for (Widget child : widgets)
		{
			applyOrbTree(child, orbPercent, boxPercent);
		}
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

	/**
	 * Fades only the dynamic children of a container (for example the side panel border pieces),
	 * leaving the container itself and its other contents alone.
	 */
	private void applyDynamicChildren(int id, int percent)
	{
		Widget container = client.getWidget(id);
		if (container == null)
		{
			return;
		}

		Widget[] children = container.getDynamicChildren();
		if (children == null)
		{
			return;
		}

		int clamped = Math.max(MIN_PERCENT, Math.min(100, percent));
		for (Widget child : children)
		{
			if (child != null)
			{
				applyWidget(child, percent, clamped);
			}
		}
	}

	private void applyTree(Widget w, int percent, int clamped)
	{
		if (w == null)
		{
			return;
		}

		if (w.getId() == Chatbox.CONTROLS_BACKGROUND_GRAPHIC)
		{
			return; // handled by applyAbsolute
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
	 * Sets a widget's opacity straight from the slider, ignoring the opacity the game gave it:
	 * 0% = fully transparent (255), 100% = fully opaque (0). At 100% the plugin stops touching the
	 * widget and puts the game's own value back, so by default nothing changes. With opaqueAt100 on,
	 * 100% instead keeps the widget fully opaque, whatever the game's own transparency setting is.
	 */
	private void applyAbsolute(int id, int percent, boolean opaqueAt100)
	{
		Widget w = client.getWidget(id);
		if (w == null)
		{
			return;
		}

		long key = ((long) w.getId() << 16) | (w.getIndex() + 1);
		State s = states.get(key);

		if (percent >= 100 && !opaqueAt100)
		{
			if (s != null)
			{
				if (s.modified && w.getOpacity() == s.lastSet)
				{
					w.setOpacity(s.original);
				}
				states.remove(key);
			}
			return;
		}

		if (s == null)
		{
			s = new State();
			states.put(key, s);
		}

		s.widget = w;
		int current = w.getOpacity();
		if (!s.modified || current != s.lastSet)
		{
			s.original = current;
		}

		int clamped = Math.max(MIN_PERCENT, Math.min(100, percent));
		int target = 255 - (255 * clamped / 100);
		if (current != target)
		{
			w.setOpacity(target);
		}

		s.lastSet = target;
		s.modified = true;
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

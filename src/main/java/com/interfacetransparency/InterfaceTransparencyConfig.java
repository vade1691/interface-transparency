package com.interfacetransparency;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

/**
 * Settings are laid out the same way in every section:
 * the grouped toggle, the "All" slider, then the individual sliders in the order
 * they appear on screen. Config keys must never be renamed (that would reset
 * users' saved settings), only the display names and descriptions.
 */
@ConfigGroup("interfacetransparency")
public interface InterfaceTransparencyConfig extends Config
{
	// ---------------------------------------------------------------------
	// Resizable modern
	// ---------------------------------------------------------------------

	@ConfigSection(
		name = "Resizable modern",
		description = "Side panel in the resizable modern layout",
		position = 0
	)
	String modernSection = "modernSection";

	@ConfigItem(
		keyName = "modernGrouped",
		name = "Use one slider for all",
		description = "On: the 'All' slider controls this whole section. Off: the individual sliders are used.",
		section = modernSection,
		position = 0
	)
	default boolean modernGrouped()
	{
		return true;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "modernOpacity",
		name = "All (%)",
		description = "Everything in this section. Used when 'Use one slider for all' is on.",
		section = modernSection,
		position = 1
	)
	default int modernOpacity()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "modernBackground",
		name = "Tab stones (%)",
		description = "The stone behind each tab icon, including the active tab. Used when 'Use one slider for all' is off.",
		section = modernSection,
		position = 2
	)
	default int modernBackground()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "modernIcons",
		name = "Tab icons (%)",
		description = "The icon on each tab. Used when 'Use one slider for all' is off.",
		section = modernSection,
		position = 3
	)
	default int modernIcons()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "modernPanelBackground",
		name = "Panel background (%)",
		description = "The background of the side panel. Used when 'Use one slider for all' is off. "
			+ "0 = transparent, 100 = the game's look, whether or not 'Transparent side panel' is on.",
		section = modernSection,
		position = 4
	)
	default int modernPanelBackground()
	{
		return 100;
	}

	@ConfigItem(
		keyName = "modernKeepPanelOpaque",
		name = "Keep panel background opaque",
		description = "At 100%, keeps the panel background solid even when the game's 'Transparent side panel' setting is on.",
		section = modernSection,
		position = 5
	)
	default boolean modernKeepPanelOpaque()
	{
		return false;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "modernBorder",
		name = "Panel border (%)",
		description = "The border around the side panel. Used when 'Use one slider for all' is off.",
		section = modernSection,
		position = 6
	)
	default int modernBorder()
	{
		return 100;
	}

	// ---------------------------------------------------------------------
	// Resizable classic
	// ---------------------------------------------------------------------

	@ConfigSection(
		name = "Resizable classic",
		description = "Side panel in the resizable classic layout",
		position = 1
	)
	String classicSection = "classicSection";

	@ConfigItem(
		keyName = "classicGrouped",
		name = "Use one slider for all",
		description = "On: the 'All' slider controls this whole section. Off: the individual sliders are used.",
		section = classicSection,
		position = 0
	)
	default boolean classicGrouped()
	{
		return true;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "classicOpacity",
		name = "All (%)",
		description = "Everything in this section. Used when 'Use one slider for all' is on.",
		section = classicSection,
		position = 1
	)
	default int classicOpacity()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "classicStones",
		name = "Tab stones (%)",
		description = "The stone behind each tab icon. Used when 'Use one slider for all' is off. "
			+ "Shares a few graphics with 'Tab icons'; the more transparent setting wins.",
		section = classicSection,
		position = 2
	)
	default int classicStones()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "classicIcons",
		name = "Tab icons (%)",
		description = "The icon on each tab. Used when 'Use one slider for all' is off. "
			+ "Shares a few graphics with 'Tab stones'; the more transparent setting wins.",
		section = classicSection,
		position = 3
	)
	default int classicIcons()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "classicPillars",
		name = "Pillars (%)",
		description = "The pillars at the sides of the panel. Used when 'Use one slider for all' is off.",
		section = classicSection,
		position = 4
	)
	default int classicPillars()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "classicBackground",
		name = "Panel background (%)",
		description = "The background of the side panel. Used when 'Use one slider for all' is off. "
			+ "0 = transparent, 100 = the game's look, whether or not 'Transparent side panel' is on.",
		section = classicSection,
		position = 5
	)
	default int classicBackground()
	{
		return 100;
	}

	@ConfigItem(
		keyName = "classicKeepPanelOpaque",
		name = "Keep panel background opaque",
		description = "At 100%, keeps the panel background solid even when the game's 'Transparent side panel' setting is on.",
		section = classicSection,
		position = 6
	)
	default boolean classicKeepPanelOpaque()
	{
		return false;
	}

	// ---------------------------------------------------------------------
	// Chat
	// ---------------------------------------------------------------------

	@ConfigSection(
		name = "Chat",
		description = "Chat tabs, chat box and dialogs",
		position = 2
	)
	String chatSection = "chatSection";

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "chatButtons",
		name = "Tab buttons (%)",
		description = "The All, Game, Public, Private, Friends, Clan and Trade buttons and the report button.",
		section = chatSection,
		position = 0
	)
	default int chatButtons()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "chatBackground",
		name = "Tab background (%)",
		description = "The strip behind the chat tab buttons. "
			+ "0 = transparent, 100 = the game's look, whether or not 'Transparent chatbox' is on.",
		section = chatSection,
		position = 1
	)
	default int chatBackground()
	{
		return 100;
	}

	@ConfigItem(
		keyName = "chatKeepBackgroundOpaque",
		name = "Keep tab background opaque",
		description = "At 100%, keeps the tab background solid even when the game's 'Transparent chatbox' setting is on.",
		section = chatSection,
		position = 2
	)
	default boolean chatKeepBackgroundOpaque()
	{
		return false;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "chatBoxBackground",
		name = "Chat box background (%)",
		description = "The background behind the chat messages. "
			+ "0 = transparent, 100 = the game's look, whether or not 'Transparent chatbox' is on.",
		section = chatSection,
		position = 3
	)
	default int chatBoxBackground()
	{
		return 100;
	}

	@ConfigItem(
		keyName = "chatKeepBoxOpaque",
		name = "Keep chat box background opaque",
		description = "At 100%, keeps the chat box background solid even when the game's 'Transparent chatbox' setting is on.",
		section = chatSection,
		position = 4
	)
	default boolean chatKeepBoxOpaque()
	{
		return false;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "chatBorder",
		name = "Chat box border (%)",
		description = "The border around the chat box and the NPC, player and option dialogs, shown when 'Transparent chatbox' is off.",
		section = chatSection,
		position = 5
	)
	default int chatBorder()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "chatDialogBackground",
		name = "Dialog background (%)",
		description = "The background behind NPC and player dialogs and the option list. "
			+ "0 = transparent, 100 = the game's look.",
		section = chatSection,
		position = 6
	)
	default int chatDialogBackground()
	{
		return 100;
	}

	// ---------------------------------------------------------------------
	// Minimap
	// ---------------------------------------------------------------------

	@ConfigSection(
		name = "Minimap",
		description = "Minimap orbs and surround",
		position = 3
	)
	String minimapSection = "minimapSection";

	@ConfigItem(
		keyName = "minimapGrouped",
		name = "Use one slider for all",
		description = "On: the 'All' slider controls this whole section. Off: the individual sliders are used.",
		section = minimapSection,
		position = 0
	)
	default boolean minimapGrouped()
	{
		return true;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "minimapOpacity",
		name = "All (%)",
		description = "Everything in this section. Used when 'Use one slider for all' is on.",
		section = minimapSection,
		position = 1
	)
	default int minimapOpacity()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "minimapOrbs",
		name = "Orbs (%)",
		description = "The orbs: icons, fills and indicators. Used when 'Use one slider for all' is off.",
		section = minimapSection,
		position = 2
	)
	default int minimapOrbs()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "minimapOrbBoxes",
		name = "Orb backgrounds (%)",
		description = "The dark boxes and discs behind the orbs. Used when 'Use one slider for all' is off.",
		section = minimapSection,
		position = 3
	)
	default int minimapOrbBoxes()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "minimapSurround",
		name = "Surround (%)",
		description = "The frame around the minimap. Used when 'Use one slider for all' is off.",
		section = minimapSection,
		position = 4
	)
	default int minimapSurround()
	{
		return 100;
	}

	@ConfigItem(
		keyName = "showUpdateMessage",
		name = "Show update message",
		description = "Show a message in the chat box once on the first login after the plugin updates",
		position = 99
	)
	default boolean showUpdateMessage()
	{
		return true;
	}
}

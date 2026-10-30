package com.interfacetransparency;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("interfacetransparency")
public interface InterfaceTransparencyConfig extends Config
{
	// ---------------------------------------------------------------------
	// Resizable modern
	// ---------------------------------------------------------------------

	@ConfigSection(
		name = "Resizable modern",
		description = "Transparency of the side panel tabs in the resizable modern layout",
		position = 0
	)
	String modernSection = "modernSection";

	@ConfigItem(
		keyName = "modernGrouped",
		name = "Use one slider for all",
		description = "On: the 'All' slider controls everything in this layout. Off: the individual sliders below are used instead.",
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
		name = "All modern elements (%)",
		description = "Used when 'Use one slider for all' is on: opacity of everything in this layout (100 = default)",
		section = modernSection,
		position = 1
	)
	default int modernOpacity()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "modernStones",
		name = "Tab stones (%)",
		description = "Used when 'Use one slider for all' is off: opacity of the dark stone behind each tab icon (100 = default)",
		section = modernSection,
		position = 2
	)
	default int modernStones()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "modernIcons",
		name = "Tab icons (%)",
		description = "Used when 'Use one slider for all' is off: opacity of the icon on each tab (100 = default)",
		section = modernSection,
		position = 3
	)
	default int modernIcons()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "modernBackground",
		name = "Panel background (%)",
		description = "Used when 'Use one slider for all' is off: opacity of the side panel background (100 = default)",
		section = modernSection,
		position = 4
	)
	default int modernBackground()
	{
		return 100;
	}

	// ---------------------------------------------------------------------
	// Resizable classic
	// ---------------------------------------------------------------------

	@ConfigSection(
		name = "Resizable classic",
		description = "Transparency of the side panel tabs in the resizable classic layout. "
			+ "Recommended: turn on 'Transparent side panel' in the game's Interfaces settings for the best result.",
		position = 1
	)
	String classicSection = "classicSection";

	@ConfigItem(
		keyName = "classicGrouped",
		name = "Use one slider for all",
		description = "On: the 'All' slider controls everything in this layout. Off: the individual sliders below are used instead.",
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
		name = "All classic elements (%)",
		description = "Used when 'Use one slider for all' is on: opacity of the tab stones, icons and panel background (100 = default)",
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
		description = "Used when 'Use one slider for all' is off: opacity of the dark stone behind each tab icon, plus side graphics 3 and 5, which are shared with the icons slider (the more transparent setting wins) (100 = default)",
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
		description = "Used when 'Use one slider for all' is off: opacity of the icon on each tab, plus side graphics 3 and 5, which are shared with the stones slider (the more transparent setting wins) (100 = default)",
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
		description = "Used when 'Use one slider for all' is off: opacity of the side pillars (100 = default)",
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
		description = "Used when 'Use one slider for all' is off: opacity of the side panel background (100 = default)",
		section = classicSection,
		position = 5
	)
	default int classicBackground()
	{
		return 100;
	}

	// ---------------------------------------------------------------------
	// Chat
	// ---------------------------------------------------------------------

	@ConfigSection(
		name = "Chat tabs",
		description = "Transparency of the chat tab buttons. "
			+ "Recommended: turn on 'Transparent chatbox' in the game's Interfaces settings for the best result.",
		position = 2
	)
	String chatSection = "chatSection";

	@ConfigItem(
		keyName = "chatGrouped",
		name = "Use one slider for all",
		description = "On: the 'All' slider controls everything in the chat tabs. Off: the individual sliders below are used instead.",
		section = chatSection,
		position = 0
	)
	default boolean chatGrouped()
	{
		return true;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "chatOpacity",
		name = "All chat elements (%)",
		description = "Used when 'Use one slider for all' is on: opacity of the chat tab buttons and their background (100 = default)",
		section = chatSection,
		position = 1
	)
	default int chatOpacity()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "chatButtons",
		name = "Tab buttons (%)",
		description = "Used when 'Use one slider for all' is off: opacity of the All, Game, Public, Private, Friends, Clan and Trade buttons and the report button (100 = default)",
		section = chatSection,
		position = 2
	)
	default int chatButtons()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "chatBackground",
		name = "Buttons background (%)",
		description = "Used when 'Use one slider for all' is off: opacity of the background behind the chat tab buttons (100 = default)",
		section = chatSection,
		position = 3
	)
	default int chatBackground()
	{
		return 100;
	}
}

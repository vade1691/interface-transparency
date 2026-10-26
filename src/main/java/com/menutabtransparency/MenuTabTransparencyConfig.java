package com.menutabtransparency;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("menutabtransparency")
public interface MenuTabTransparencyConfig extends Config
{
	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "modernOpacity",
		name = "Resizable modern opacity (%)",
		description = "Opacity of the tab stones, icons and panel background in the resizable modern layout (100 = default)",
		position = 0
	)
	default int modernOpacity()
	{
		return 100;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "classicOpacity",
		name = "Resizable classic opacity (%)",
		description = "Opacity of the tab stones, icons and panel background in the resizable classic layout (100 = default). "
			+ "Recommended: turn on 'Transparent side panel' in the game's Interfaces settings for the best result.",
		position = 1
	)
	default int classicOpacity()
	{
		return 100;
	}
}

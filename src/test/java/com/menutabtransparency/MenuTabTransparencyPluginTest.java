package com.menutabtransparency;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MenuTabTransparencyPluginTest
{
public static void main(String[] args) throws Exception
{
ExternalPluginManager.loadBuiltin(MenuTabTransparencyPlugin.class);
RuneLite.main(args);
}
}

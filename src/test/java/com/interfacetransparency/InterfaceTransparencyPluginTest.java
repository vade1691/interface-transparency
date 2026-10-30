package com.interfacetransparency;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class InterfaceTransparencyPluginTest
{
public static void main(String[] args) throws Exception
{
ExternalPluginManager.loadBuiltin(InterfaceTransparencyPlugin.class);
RuneLite.main(args);
}
}

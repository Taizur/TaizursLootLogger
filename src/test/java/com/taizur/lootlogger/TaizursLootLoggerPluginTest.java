package com.taizur.lootlogger;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TaizursLootLoggerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TaizursLootLoggerPlugin.class);
		RuneLite.main(args);
	}
}
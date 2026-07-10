package com.taizur.lootlogger;

import com.google.inject.Provides;
import javax.inject.Inject;

import net.runelite.api.ItemComposition;
import net.runelite.api.NPC;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.events.NpcLootReceived;


@PluginDescriptor(
	name = "Taizur's Loot Logger"
)
public class TaizursLootLoggerPlugin extends Plugin
{
	ItemStack loot;
	String name;
	int amount;
	int price;

	@Inject
	private TaizursLootLoggerPluginConfig config;

	@Inject
	private ItemManager itemManager;

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived lootEvent)
	{
		NPC mob = lootEvent.getNpc();
		System.out.println("NPC: " + mob.getName());
		for(ItemStack loot: lootEvent.getItems())
		{

			int id = loot.getId();
			int quantity = loot.getQuantity();
			ItemComposition composition = itemManager.getItemComposition(id);
			String name = composition.getName();
			System.out.println("Loot: " + quantity + " x " + name);
		}
	}



	@Provides
	TaizursLootLoggerPluginConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TaizursLootLoggerPluginConfig.class);
	}
}

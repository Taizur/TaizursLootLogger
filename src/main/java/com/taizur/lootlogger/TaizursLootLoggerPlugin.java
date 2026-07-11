package com.taizur.lootlogger;

import com.google.inject.Provides;
import javax.inject.Inject;

import net.runelite.api.ItemComposition;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.events.NpcLootReceived;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.Collection;


@PluginDescriptor(
	name = "Taizur's Loot Logger"
)
@Slf4j
public class TaizursLootLoggerPlugin extends Plugin
{

	@Inject
	private TaizursLootLoggerPluginConfig config;

	@Inject
	private ItemManager itemManager;

	@Inject
	private DropLedger ledger;

	@Inject
	private CsvDropRepository repository;

	@Inject
	private ClientThread clientThread;

	@Override
	protected void startUp() throws IOException
	{
		repository.initialize();

		Collection<DropTotal> loadedDrops = repository.load();

		ledger.loadDrops(loadedDrops);

		clientThread.invokeLater(() ->
		{
			ledger.updatePrices();

			try
			{
				repository.save(ledger.getAllDrops());
			}
			catch (IOException e)
			{
				log.error("Failed to save loot data", e);
			}
		});
	}

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived lootEvent)
	{
		for(ItemStack loot: lootEvent.getItems())
		{

			int id = loot.getId();
			long quantity = loot.getQuantity();
			ItemComposition composition = itemManager.getItemComposition(id);
			String name = composition.getName();
			ledger.addDrop(id, name, quantity);

		}
		try
		{
			repository.save(ledger.getAllDrops());
		}
		catch (IOException e) {
			log.error("Failed to save loot data", e);
		}
	}

	@Override
	protected void shutDown() throws IOException
	{
		repository.save(ledger.getAllDrops());
	}

	@Provides
	TaizursLootLoggerPluginConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TaizursLootLoggerPluginConfig.class);
	}
}

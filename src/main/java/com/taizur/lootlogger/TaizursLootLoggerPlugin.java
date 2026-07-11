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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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

	private ExecutorService fileExecutor;

	@Override
	protected void startUp()
	{
		fileExecutor = Executors.newSingleThreadExecutor();

		fileExecutor.submit(() ->
		{
			try
			{
				repository.initialize();
				Collection<DropTotal> loadedDrops = repository.load();

				clientThread.invoke(() ->
				{
					ledger.loadDrops(loadedDrops);
					ledger.updatePrices();
					Collection<DropTotal> snapshot = ledger.snapshotDrops();

					fileExecutor.submit(() ->
					{
						try
						{
							repository.save(snapshot);
						}
						catch (IOException e)
						{
							log.error("Failed to save refreshed loot prices", e);
						}
					});
				});
			}
			catch (IOException e)
			{
				log.error("Failed to load loot data", e);
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
		Collection<DropTotal> snapshot = ledger.snapshotDrops();

		fileExecutor.submit(() ->
		{
			try
			{
				repository.save(snapshot);
			}
			catch (IOException e)
			{
				log.error("Failed to save loot data", e);
			}
		});
	}

	@Override
	protected void shutDown()
	{
		fileExecutor.shutdownNow();
	}

	@Provides
	TaizursLootLoggerPluginConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TaizursLootLoggerPluginConfig.class);
	}
}

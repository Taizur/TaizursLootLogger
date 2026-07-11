package com.taizur.lootlogger;


import javax.inject.Inject;
import net.runelite.api.ItemComposition;
import net.runelite.client.callback.ClientThread;
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
	name = "CSV Lifetime Drop Ledger"
)
@Slf4j
public class TaizursLootLoggerPlugin extends Plugin
{

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
		ExecutorService executor = Executors.newSingleThreadExecutor();
		fileExecutor = executor;

		executor.submit(() ->
		{
			try
			{
				repository.initialize();
				Collection<DropTotal> loadedDrops = repository.load();

				clientThread.invoke(() ->
				{
					if (fileExecutor != executor || executor.isShutdown())
					{
						return;
					}

					ledger.loadDrops(loadedDrops);
					ledger.updatePrices();
					Collection<DropTotal> snapshot = ledger.snapshotDrops();

					executor.submit(() ->
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
		ledger.clear();
	}


}

package com.taizur.lootlogger;

import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class DropLedger {
    private final Map<Integer, DropTotal> drops;
    @Inject
    private ItemManager itemManager;

    public DropLedger()
    {
        drops = new HashMap<>();
    }

    public void addDrop(int itemId, String itemName, long quantity)
    {
        if (drops.containsKey(itemId))
        {
            drops.get(itemId).addQuantity(quantity);
        }
        else
        {
            addNewDrop(itemId, itemName, quantity);
        }
    }

    private void addNewDrop(int itemId, String itemName, long quantity)
    {
        ItemComposition composition = itemManager.getItemComposition(itemId);
        boolean tradeable = composition.isGeTradeable();

        int gePrice = itemManager.getItemPriceWithSource(itemId, true);

        DropTotal newDrop = new DropTotal(itemId, itemName, tradeable, quantity, gePrice);
        drops.put(itemId, newDrop);
    }

    public DropTotal getDrop(int itemId)
    {
        return drops.get(itemId);
    }

    public void updatePrices()
    {
        for (DropTotal drop: drops.values())
        {
            int newPrice = itemManager.getItemPriceWithSource(drop.getItemId(), true);
            drop.setGePrice(newPrice);
        }
    }

    public Collection<DropTotal> getAllDrops()
    {
        return drops.values();
    }

    public void loadDrops(Collection<DropTotal> loadedDrops)
    {
        for (DropTotal loadedDrop : loadedDrops)
        {
            DropTotal existingDrop = drops.get(loadedDrop.getItemId());

            if (existingDrop == null)
            {
                drops.put(loadedDrop.getItemId(), loadedDrop);
            }
            else
            {
                existingDrop.addQuantity(loadedDrop.getTotalQuantity());
            }
        }
    }

    public Collection<DropTotal> snapshotDrops()
    {
        Collection<DropTotal> snapshot = new ArrayList<>();

        for (DropTotal drop : drops.values())
        {
            snapshot.add(new DropTotal(
                    drop.getItemId(),
                    drop.getItemName(),
                    drop.isTradeable(),
                    drop.getTotalQuantity(),
                    drop.getGePrice()
            ));
        }

        return snapshot;
    }

    public void clear()
    {
        drops.clear();
    }
}

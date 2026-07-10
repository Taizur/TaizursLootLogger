package com.taizur.lootlogger;

import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

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
}

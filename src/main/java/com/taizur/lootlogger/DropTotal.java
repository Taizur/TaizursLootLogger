package com.taizur.lootlogger;

import lombok.Getter;

@Getter
public class DropTotal
{
    private final int itemId;
    private final String itemName;
    private final boolean tradeable;
    private long totalQuantity;
    private int gePrice;

    public DropTotal(int itemId, String itemName, boolean tradeable, long totalQuantity, int gePrice)
    {
        this.itemId = itemId;
        this.itemName = itemName;
        this.tradeable = tradeable;
        this.totalQuantity = totalQuantity;
        this.gePrice = gePrice;
    }

    public void addQuantity(long quantity)
    {
        totalQuantity += quantity;
    }

    public void setGePrice(int gePrice) {
        this.gePrice = gePrice;
    }
}

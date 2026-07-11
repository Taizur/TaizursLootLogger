package com.taizur.lootlogger;

import java.io.File;
import java.io.IOException;
import net.runelite.client.RuneLite;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvDropRepository
{
    private final File pluginDirectory;
    private final File csvFile;

    public CsvDropRepository()
    {
        pluginDirectory = new File(RuneLite.RUNELITE_DIR, "taizurs-loot-logger");
        csvFile = new File(pluginDirectory, "loot-log.csv");
    }

    public void initialize() throws IOException
    {
        if (!pluginDirectory.exists())
        {
            boolean directoryCreated = pluginDirectory.mkdirs();

            if (!directoryCreated)
            {
                throw new IOException("Failed to create plugin directory.");
            }
        }

        if (!csvFile.exists())
        {
            boolean fileCreated = csvFile.createNewFile();

            if (!fileCreated)
            {
                throw new IOException("Failed to create loot log file.");
            }
        }
    }

    public void save(Collection<DropTotal> drops) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile)))
        {
            writer.write("Item ID,Item Name,Tradeable,Total Quantity,GE Price");
            writer.newLine();

            for (DropTotal drop: drops)
            {
                String row =
                                drop.getItemId() + "," +
                                drop.getItemName() + "," +
                                drop.isTradeable() + "," +
                                drop.getTotalQuantity() + "," +
                                drop.getGePrice();
                writer.write(row);
                writer.newLine();
            }
        }
    }

    public Collection<DropTotal> load() throws IOException
    {
        Collection<DropTotal> loadedDrops = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile)))
        {
            String line = reader.readLine(); // skip header

            while ((line = reader.readLine()) != null)
            {
                if (line.isBlank())
                {
                    continue;
                }

                String[] values = line.split(",");

                if (values.length != 5)
                {
                    log.warn("Skipping malformed loot log row: {}", line);
                    continue;
                }

                try
                {
                    int itemId = Integer.parseInt(values[0]);
                    String itemName = values[1];
                    boolean tradeable = Boolean.parseBoolean(values[2]);
                    long totalQuantity = Long.parseLong(values[3]);
                    int gePrice = Integer.parseInt(values[4]);

                    DropTotal drop = new DropTotal(
                            itemId,
                            itemName,
                            tradeable,
                            totalQuantity,
                            gePrice
                    );

                    loadedDrops.add(drop);
                }
                catch (NumberFormatException e)
                {
                    log.warn("Skipping malformed loot log row: {}", line);
                }
            }
        }

        return loadedDrops;
    }
}

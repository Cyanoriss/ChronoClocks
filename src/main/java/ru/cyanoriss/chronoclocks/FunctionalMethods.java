package ru.cyanoriss.chronoclocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FunctionalMethods {

    /**
     * Запускает новые часы
     * @param clock название подраздела в конфиге
     */
    public static void startClock(String clock) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ConfigurationSection section = Main.getInstance().getConfig().getConfigurationSection("clocks." + clock);

                if (section == null) {
                    cancel();
                    return;
                }

                Location startLocation = new Location(
                        Bukkit.getWorld(section.getString("location.world")),
                        section.getInt("location.x"),
                        section.getInt("location.y"),
                        section.getInt("location.z")
                );
                Material numbersBlock = Material.valueOf(section.getString("numbers-material").toUpperCase());
                Material backgroundBlock = Material.valueOf(section.getString("background-material").toUpperCase());
                String direction = section.getString("direction");
                String font = section.getString("font");
                int size = section.getInt("size");
                int spacing = section.getInt("spacing");
                LocalDateTime rawTime = LocalDateTime.now(ZoneId.of(section.getString("timezone")));
                DateTimeFormatter format = DateTimeFormatter.ofPattern(section.getString("pattern"));
                String formattedTime = rawTime.format(format)
                        .replaceAll("4", "Ч");
                placeClocks(startLocation, direction, formattedTime, size, spacing, font, numbersBlock, backgroundBlock);
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    /**
     * Ставит в мире текст из блоков
     */
    public static void placeClocks(Location startLocation, String direction, String text, int size, int spacing, String fontName, Material numbers, Material background) {
        Location location = startLocation.clone();
        for (int index = 0; index < text.length(); index++) {
            Font font = new Font(fontName, Font.PLAIN, size);
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int x = (size - fm.stringWidth(String.valueOf(text.charAt(index)))) / 2;
            int y = (size - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(String.valueOf(text.charAt(index)), x, y);
            g2d.dispose();

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int pixel = image.getRGB(j, i);
                    Block block;
                    if (direction.equalsIgnoreCase("north")) {
                        block = location.getWorld().getBlockAt(location.clone().add(j, -i, 0));
                    } else if (direction.equalsIgnoreCase("south")) {
                        block = location.getWorld().getBlockAt(location.clone().add(-j, -i, 0));
                    } else if (direction.equalsIgnoreCase("west")) {
                        block = location.getWorld().getBlockAt(location.clone().add(0, -i, -j));
                    } else {
                        block = location.getWorld().getBlockAt(location.clone().add(0, -i, j));
                    }
                    if ((pixel >> 24 & 0xFF) != 0x00) {
                        block.setType(numbers);
                    } else {
                        block.setType(background);
                    }
                }
            }

            if (direction.equalsIgnoreCase("north")) {
                location.add(spacing, 0, 0);
            } else if (direction.equalsIgnoreCase("south")) {
                location.add(-spacing, 0, 0);
            } else if (direction.equalsIgnoreCase("west")) {
                location.add(0, 0, -spacing);
            } else {
                location.add(0, 0, spacing);
            }
        }
    }
}

package ru.cyanoriss.chronoclocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class Main extends JavaPlugin {
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        for (String clock : getConfig().getConfigurationSection("clocks").getKeys(false)) {
            FunctionalMethods.startClock(clock);
        }

        getCommand("clocks").setExecutor(new ClocksCommand(instance));
        getCommand("clocks").setTabCompleter(new ClocksCommand(instance));
    }
}

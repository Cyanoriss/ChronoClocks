package ru.cyanoriss.chronoclocks;

import org.bukkit.plugin.java.JavaPlugin;

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
        getCommand("test").setExecutor(new TestCommand());
        LocalDateTime rawTime = LocalDateTime.now(ZoneId.of("UTC+4"));

        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");

        String formattedTime = rawTime.format(format);

        getLogger().info(formattedTime);

    }
}

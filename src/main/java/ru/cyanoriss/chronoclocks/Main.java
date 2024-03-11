package ru.cyanoriss.chronoclocks;

import org.bukkit.plugin.java.JavaPlugin;

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

package ru.cyanoriss.chronoclocks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class ClocksCommand implements TabExecutor {
    private final Main plugin;

    public ClocksCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!sender.hasPermission(plugin.getConfig().getString("permission"))
        || !(sender instanceof Player)) {
            return List.of();
        }

        if (args.length == 1) {
            return List.of("create", "delete", "edit", "movehere");
        }

        return null;
    }
}

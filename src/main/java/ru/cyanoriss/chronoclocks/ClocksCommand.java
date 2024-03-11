package ru.cyanoriss.chronoclocks;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import ru.cyanoriss.chronoclocks.util.Config;
import ru.cyanoriss.chronoclocks.util.Hex;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClocksCommand implements TabExecutor {
    private final Main plugin;

    public ClocksCommand(Main plugin) {
        this.plugin = plugin;
    }

    private void sendUsage(CommandSender player) {
        for (String line : plugin.getConfig().getStringList("messages.usage")) {
            player.sendMessage(Hex.color(line));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Hex.color("&cЭта команда только для игроков"));
            return true;
        }

        if (!player.hasPermission(plugin.getConfig().getString("permission"))) {
            sender.sendMessage(Config.getMessage("no-perms"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 9) {
                sendUsage(player);
                return true;
            }

            try {
                Integer.parseInt(args[4]);
                Integer.parseInt(args[5]);
                Material.valueOf(args[6].toUpperCase());
                Material.valueOf(args[7].toUpperCase());
            } catch (Exception e) {
                player.sendMessage(Config.getMessage("internal-error"));
                return true;
            }

            if (plugin.getConfig().getConfigurationSection("clocks." + args[1]) != null) {
                plugin.getConfig().set("clocks." + args[1], null);
                plugin.saveConfig();
            }

            plugin.getConfig().set("clocks." + args[1] + ".pattern", args[2]);
            plugin.getConfig().set("clocks." + args[1] + ".timezone", args[3]);
            plugin.getConfig().set("clocks." + args[1] + ".size", Integer.parseInt(args[4]));
            plugin.getConfig().set("clocks." + args[1] + ".spacing", Integer.parseInt(args[5]));
            plugin.getConfig().set("clocks." + args[1] + ".numbers-material", args[6]);
            plugin.getConfig().set("clocks." + args[1] + ".background-material", args[7]);

            StringBuilder sb = new StringBuilder();
            for (int i = 7; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }

            plugin.getConfig().set("clocks." + args[1] + ".font", sb.toString().trim());
            changeLocation(args, player);

            player.sendMessage(Config.getMessage("create-success")
                    .replace("%name%", args[1])
                    );

            FunctionalMethods.startClock(args[1]);
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (checkSubCommands(args, player)) return true;

            plugin.getConfig().set("clocks." + args[1], null);
            plugin.saveConfig();

            player.sendMessage(Config.getMessage("delete-success")
                    .replace("%name%", args[1])
            );
            return true;
        }

        if (args[0].equalsIgnoreCase("movehere")) {
            if (checkSubCommands(args, player)) return true;

            changeLocation(args, player);
            player.sendMessage(Config.getMessage("movehere-success")
                    .replace("%name%", args[1])
            );
            return true;
        }

        if (args[0].equalsIgnoreCase("edit")) {
            if (checkSubCommands(args, player)) return true;

            if (args.length < 4) {
                sendUsage(player);
            }

            if (args[2].equalsIgnoreCase("background-material")
                    || args[2].equalsIgnoreCase("numbers-material")) {
                try {
                    Material.valueOf(args[3].toUpperCase());
                } catch (Exception e) {
                    player.sendMessage(Config.getMessage("internal-error"));
                    return true;
                }
            }

            if (args[2].equalsIgnoreCase("size")
                    || args[2].equalsIgnoreCase("spacing")) {
                try {
                    Integer.parseInt(args[3]);
                    plugin.getConfig().set(args[2], Integer.parseInt(args[3]));
                    plugin.saveConfig();
                    player.sendMessage(Config.getMessage("edit-success"));
                    return true;
                } catch (NumberFormatException e) {
                    player.sendMessage(Config.getMessage("internal-error"));
                    return true;
                }
            }

            plugin.getConfig().set("clocks." + args[1] + "." + args[2], args[3]);
            plugin.saveConfig();
            player.sendMessage(Config.getMessage("edit-success")
                    .replace("%name%", args[1])
            );
            return true;
        }

        return true;
    }

    /**
     * Проверка для удаления, изменения и перемещения часов
     */
    private boolean checkSubCommands(String[] args, Player player) {
        if (args.length < 2) {
            sendUsage(player);
            return true;
        }

        if (plugin.getConfig().getConfigurationSection("clocks." + args[1]) == null) {
            player.sendMessage(Config.getMessage("clocks-not-found")
                    .replace("%name%", args[1])
            );
            return true;
        }
        return false;
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

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length == 2) {
                return List.of("название");
            }

            if (args.length == 3) {
                return List.of("HH:mm:ss", "HH:mm");
            }

            if (args.length == 4) {
                return List.of("UTC+1", "UTC+2", "UTC+3", "UTC+4", "UTC+5");
            }

            if (args.length == 5) {
                return List.of("8", "16", "24");
            }

            if (args.length == 6) {
                return List.of("6", "10", "20");
            }

            if (args.length == 7 || args.length == 8) {
                List<String> materials = new ArrayList<>();
                for (Material material : Material.values()) {
                    materials.add(material.name().toLowerCase());
                }
                return materials;
            }

            if (args.length == 9) {
                return List.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
            }
        }

        if (args[0].equalsIgnoreCase("edit")) {
            if (args.length == 2) {
                return plugin.getConfig().getConfigurationSection("clocks").getKeys(false).stream().toList();
            }

            if (args.length == 3) {
                return List.of("background-material", "numbers-material", "font", "pattern", "size",
                        "spacing", "timezone");
            }

            if (args.length == 4) {
                return List.of("значение");
            }
        }

        if (args[0].equalsIgnoreCase("delete")
        || args[0].equalsIgnoreCase("movehere")) {
            if (args.length == 2) {
                return plugin.getConfig().getConfigurationSection("clocks").getKeys(false).stream().toList();
            }
        }

        return List.of();
    }

    private void changeLocation(String[] args, Player player) {
        String direction = null;

        switch (player.getFacing()) {
            case NORTH -> direction = "north";
            case SOUTH -> direction = "south";
            case WEST -> direction = "west";
            case EAST -> direction = "east";
        }

        if (direction == null) {
            player.sendMessage(Config.getMessage("look-not-exists"));
        }

        plugin.getConfig().set("clocks." + args[1] + ".direction", direction);
        plugin.getConfig().set("clocks." + args[1] + ".location.world", player.getWorld().getName());
        plugin.getConfig().set("clocks." + args[1] + ".location.x", (int) player.getLocation().getX());
        plugin.getConfig().set("clocks." + args[1] + ".location.y", (int) player.getLocation().getY());
        plugin.getConfig().set("clocks." + args[1] + ".location.z", (int) player.getLocation().getZ());
        plugin.saveConfig();
    }
}

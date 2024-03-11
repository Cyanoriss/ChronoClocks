package ru.cyanoriss.chronoclocks.util;

import ru.cyanoriss.chronoclocks.Main;

import java.util.List;

public class Config {
    public static String getMessage(String path) {
        return Hex.color(Main.getInstance().getConfig().getString("messages." + path));
    }

    public static String getString(String path) {
        return Hex.color(Main.getInstance().getConfig().getString(path));
    }

    public static List<String> getList(String path) {
        return Hex.color(Main.getInstance().getConfig().getStringList(path));
    }

    public static boolean getBoolean(String path) {
        return Main.getInstance().getConfig().getBoolean(path);
    }

    public static int getInt(String path) {
        return Main.getInstance().getConfig().getInt(path);
    }

    public static double getDouble(String path) {
        return Main.getInstance().getConfig().getDouble(path);
    }
}

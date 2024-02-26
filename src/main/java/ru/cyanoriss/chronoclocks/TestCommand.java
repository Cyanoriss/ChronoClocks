package ru.cyanoriss.chronoclocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        int size = Integer.parseInt(args[1]);
        String symbols = args[0];

        // todo: пофиксить эту хуйню
        File fontFile = new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/plugins/" + Main.getInstance().getName() + "/arial.ttf");

        StringBuilder fontName = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            if (i != 2) fontName.append(" ");
            fontName.append(args[i]);
        }
        Location location = player.getLocation().add(1, 0, 0);

        for (int index = 0; index < symbols.length(); index++) {

            Font font = new Font(fontName.toString(), Font.PLAIN, size);
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int x = (size - fm.stringWidth(String.valueOf(symbols.charAt(index)))) / 2;
            int y = (size - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(String.valueOf(symbols.charAt(index)), x, y);
            g2d.dispose();


            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int pixel = image.getRGB(j, i);
                    if ((pixel >> 24 & 0xFF) != 0x00) {
                        location.getWorld().getBlockAt(location.clone().add(j, -i, 0)).setType(Material.BLACK_CONCRETE);
                    }
                }
            }

            location.add(size, 0, 0);
        }

        return true;
    }
}

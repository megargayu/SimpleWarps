package me.megargayu.simplewarps.commands;

import me.megargayu.simplewarps.SimpleWarps;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SubcommandSet {
    public static boolean handleSet(SimpleWarps plugin, CommandSender sender, String[] args, Set<Character> validWarpChars) {
        // Only players can set warps
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("setwarp.onlyPlayers", true));
            return false;
        }

        // Check permission
        Player player = (Player) sender;
        if (!player.hasPermission("simplewarps.setwarp")) {
            player.sendMessage(plugin.getMessage("setwarp.noPermissionMessage", true));
            return false;
        }

        // Check args
        if (args.length < 2) {
            player.sendMessage(plugin.getUsage(sender));
            return false;
        }

        // Make sure warp id is not too long
        int maxWarpIdLength = plugin.getConfig().getInt("maxWarpIdLength");
        if (maxWarpIdLength > 0 && args[1].length() > maxWarpIdLength) {
            player.sendMessage(plugin.getMessage("setwarp.tooLong", true));
            return false;
        }


        // Check if valid
        Set<Character> charsInId = new HashSet<>();
        for (char c : args[1].toCharArray()) charsInId.add(c);
        if (!validWarpChars.containsAll(charsInId)) {
            player.sendMessage(plugin.getMessage("setwarp.invalidChar", true));
            return false;
        }

        // Make sure warp with that id is not already created
        if (plugin.warpsConfig.getKeys(false).contains(args[1])) {
            player.sendMessage(plugin.getMessage("setwarp.duplicateWarp", true));
            return false;
        }

        Location location = player.getLocation();
        plugin.warpsConfig.set(args[1] + ".coordX", location.getX());
        plugin.warpsConfig.set(args[1] + ".coordY", location.getY());
        plugin.warpsConfig.set(args[1] + ".coordZ", location.getZ());
        plugin.warpsConfig.set(args[1] + ".yaw", location.getYaw());
        plugin.warpsConfig.set(args[1] + ".pitch", location.getPitch());
        plugin.warpsConfig.set(args[1] + ".world", Objects.requireNonNull(location.getWorld()).getName());

        try {
            plugin.warpsConfig.save(plugin.warpsConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        player.sendMessage(plugin.getMessage("setwarp.success", true));
        return true;
    }
}

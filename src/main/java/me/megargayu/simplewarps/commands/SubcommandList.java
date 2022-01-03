package me.megargayu.simplewarps.commands;

import me.megargayu.simplewarps.SimpleWarps;
import org.bukkit.command.CommandSender;

public class SubcommandList {
    public static boolean handleList(SimpleWarps plugin, CommandSender sender) {
        // Check permission
        if (!sender.hasPermission("simplewarps.listwarps")) {
            sender.sendMessage(plugin.getMessage("listwarps.noPermissionMessage", true));
            return false;
        }

        // Check if there are no warps
        if (plugin.warpsConfig.getKeys(false).size() == 0) {
            sender.sendMessage(plugin.getMessage("listwarps.noWarps", true));
            return true;
        }

        StringBuilder message = new StringBuilder(plugin.getMessage("listwarps.listPrefix", true))
                .append(' ');
        for (String warp : plugin.warpsConfig.getKeys(false))
            message.append(warp).append(", ");

        sender.sendMessage(message.substring(0, message.length() - 2));
        return true;
    }
}

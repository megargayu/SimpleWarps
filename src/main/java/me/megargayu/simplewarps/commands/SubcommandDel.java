package me.megargayu.simplewarps.commands;

import me.megargayu.simplewarps.SimpleWarps;
import org.bukkit.command.CommandSender;

public class SubcommandDel {
    public static boolean handleDel(SimpleWarps plugin, CommandSender sender, String[] args) {
        // Check permission
        if (!sender.hasPermission("simplewarps.delwarp")) {
            sender.sendMessage(plugin.getMessage("delwarp.noPermissionMessage", true));
            return false;
        }

        // Check args
        if (args.length < 2) {
            sender.sendMessage(plugin.getUsage(sender));
            return false;
        }

        if (!plugin.warpsConfig.contains(args[1])) {
            sender.sendMessage(plugin.getMessage("delwarp.noExist", true));
            return false;
        }

        plugin.warpsConfig.set(args[1], (Object) null);
        sender.sendMessage(plugin.getMessage("delwarp.success", true));
        return true;
    }
}

package me.megargayu.simplewarps.commands;

import me.megargayu.simplewarps.SimpleWarps;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandWarps implements CommandExecutor {
    private final SimpleWarps plugin;

    public CommandWarps(SimpleWarps plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Warps command only lists warps, and needs 0 arguments.
        if (args.length > 0) {
            sender.sendMessage(plugin.getUsage(sender));
            return false;
        }

        // Just an alias for /warp list, so all logic is in that command
        return SubcommandList.handleList(plugin, sender);
    }
}

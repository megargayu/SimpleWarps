package me.megargayu.simplewarps.commands;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import me.megargayu.simplewarps.SimpleWarps;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CommandWarp implements CommandExecutor, TabCompleter {
    private final SimpleWarps plugin;
    private Set<Character> validWarpChars;

    public CommandWarp(SimpleWarps plugin) {
        this.plugin = plugin;
        this.populateValidChars();
    }

    private void populateValidChars() {
        // Use HashSet to compare valid characters (fast)
        this.validWarpChars = new HashSet<>();
        for (char c : Objects.requireNonNull(this.plugin.getConfig().getString("validWarpChars")).toCharArray())
            this.validWarpChars.add(c);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // No command or subcommand requires 0 args
        if (args.length == 0) {
            sender.sendMessage(plugin.getUsage(sender));
            return false;
        }

        switch (args[0]) {
            case "help":
            case "usage":
                sender.sendMessage(plugin.getUsage(sender));
                return true;

            case "list":
                return SubcommandList.handleList(plugin, sender);

            case "set":
            case "create":
            case "add":
                return SubcommandSet.handleSet(plugin, sender, args, validWarpChars);

            case "del":
            case "delete":
            case "remove":
                return SubcommandDel.handleDel(plugin, sender, args);

            case "reload":
                if (!sender.hasPermission("warps.reload")) {
                    sender.sendMessage(plugin.getUsage(sender));
                    return false;
                }

                plugin.reloadConfig();
                populateValidChars();
                sender.sendMessage(plugin.getMessage("reload.success", true));
                return true;

            default:
                return handleWarp(sender, args);
        }
    }

    private boolean handleWarp(CommandSender sender, final String[] args) {
        // Only players can warp
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("warp.onlyPlayers", true));
            return false;
        }

        // Check permission
        Player player = (Player) sender;
        if (!player.hasPermission("simplewarps.warp")) {
            player.sendMessage(plugin.getMessage("warp.noPermissionMessage", true));
            return false;
        }

        // Make sure the warp actually exists
        // (no need to check args length as that is already done in onCommand)
        if (!plugin.warpsConfig.contains(args[0])) {
            player.sendMessage(plugin.getMessage("warp.noExist", true));
            return false;
        }

        // Get number of seconds and send wait message if greater than 0
        int seconds = player.hasPermission("simplewarps.instantwarp") ? 0 :
                Math.abs(plugin.getConfig().getInt("warpTime"));
        if (seconds > 0)
            player.sendMessage(plugin.getMessage("warp.wait", true,
                    ImmutableMap.of("time", String.valueOf(seconds))));

        // Warp-er
        Location location = player.getLocation();
        new BukkitRunnable() {
            public void run() {
                int maxMoveDist = plugin.getConfig().getInt("maxMoveDist");
                if (player.hasPermission("simplewarps.move") || maxMoveDist < 0 || seconds == 0 ||
                        player.getLocation().distanceSquared(location) <= maxMoveDist * maxMoveDist) {
                    ConfigurationSection warp = plugin.warpsConfig.getConfigurationSection(args[0]);
                    assert warp != null;

                    // Teleport
                    player.teleport(new Location(plugin.getServer()
                            .getWorld(Objects.requireNonNull(warp.getString("world"))),
                            warp.getDouble("coordX"),
                            warp.getDouble("coordY"),
                            warp.getDouble("coordZ"),
                            (float) warp.getDouble("yaw"),
                            (float) warp.getDouble("pitch")));

                    player.sendMessage(plugin.getMessage("warp.success", true));
                } else
                    player.sendMessage(plugin.getMessage("warp.moved", true,
                            ImmutableMap.of("maxMoveDist", String.valueOf(maxMoveDist))));

            }
        }.runTaskLater(plugin, 20L * (long) seconds);

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            ArrayList<String> validCommands = new ArrayList<>();
            if (sender.hasPermission("simplewarps.listwarps"))
                validCommands.add("list");

            if (sender.hasPermission("simplewarps.setwarp")) {
                validCommands.add("set");
                validCommands.add("create");
                validCommands.add("add");
            }

            if (sender.hasPermission("simplewarps.delwarp")) {
                validCommands.add("del");
                validCommands.add("delete");
                validCommands.add("remove");
            }

            if (sender.hasPermission("simplewarps.warp")) {
                validCommands.addAll(this.plugin.warpsConfig.getKeys(false));
            }

            return validCommands;
        }

        if (args.length == 2 && (args[0].equals("del") || args[0].equals("delete") || args[0].equals("remove")))
            return Lists.newArrayList(this.plugin.warpsConfig.getKeys(false));

        return new ArrayList<>();
    }
}

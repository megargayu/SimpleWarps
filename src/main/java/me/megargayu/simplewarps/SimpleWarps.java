package me.megargayu.simplewarps;

import me.megargayu.simplewarps.commands.CommandWarp;
import me.megargayu.simplewarps.commands.CommandWarps;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public final class SimpleWarps extends JavaPlugin {
    public File warpsConfigFile;
    public FileConfiguration warpsConfig;

    public void onEnable() {
        // Save default config
        this.saveDefaultConfig();

        // Create warps config
        this.warpsConfigFile = new File(this.getDataFolder(), "warps.yml");
        if (!warpsConfigFile.exists()) {
            warpsConfigFile.getParentFile().mkdirs();
            saveResource("warps.yml", false);
        }

        warpsConfig = new YamlConfiguration();
        warpsConfig.options().copyDefaults(false);
        try {
            warpsConfig.load(this.warpsConfigFile);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
            return;
        }

        // Register warp and warps commands
        Objects.requireNonNull(this.getCommand("warp")).setExecutor(new CommandWarp(this));
        Objects.requireNonNull(this.getCommand("warps")).setExecutor(new CommandWarps(this));
    }

    private String getMessage(String path) {
        String raw = Objects.requireNonNull(Objects.requireNonNull(getConfig()
                .getConfigurationSection("messages")).getString(path));
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    public String getMessage(String path, boolean addPrefix) {
        return addPrefix ? getMessage("pluginPrefix") + " " + getMessage(path) : getMessage(path);
    }

    public String getMessage(String path, boolean addPrefix, Map<String, String> variables) {
        return new StrSubstitutor(variables).replace(getMessage(path, addPrefix));
    }

    public String getUsage(CommandSender sender) {
        StringBuilder builder = new StringBuilder(getMessage("help.usagePrefix")).append('\n')
                .append(getMessage("help.usage")).append('\n');

        if (sender.hasPermission("simplewarps.listwarps"))
            builder.append(getMessage("listwarps.usage")).append("\n");

        if (sender.hasPermission("simplewarps.setwarp"))
            builder.append(getMessage("setwarp.usage")).append("\n");

        if (sender.hasPermission("simplewarps.delwarp"))
            builder.append(getMessage("delwarp.usage")).append("\n");

        if (sender.hasPermission("simplewarps.warp"))
            builder.append(getMessage("warp.usage")).append("\n");

        if (sender.hasPermission("simplewarps.reload"))
            builder.append(getMessage("reload.usage")).append("\n");

        return builder.substring(0, builder.length() - 1);
    }
}

package com.flyaway.anvilboundless;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilBoundless extends JavaPlugin {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
        getCommand("anvilboundless").setExecutor(this);
        getLogger().info("AnvilBoundless включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AnvilBoundless выключен!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("anvilboundless.reload")) {
                sender.sendMessage(miniMessage.deserialize("<red>You don't have permissions."));
                return true;
            }

            reloadConfig();
            config = getConfig();
            sender.sendMessage(miniMessage.deserialize("<green>AnvilBoundless - Config reloaded."));
            return true;
        }

        sender.sendMessage(miniMessage.deserialize("<yellow>Use: /anvilboundless reload"));
        return true;
    }

    public boolean isBypassTooExpensive() {
        return config.getBoolean("bypass-too-expensive", true);
    }

    public int getMaxRepairCost() {
        return config.getInt("max-repair-cost", 39);
    }

    public boolean isKeepOverleveledEnchants() {
        return config.getBoolean("keep-overleveled-enchants", true);
    }
}

package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.*;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;
import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

abstract class CheatPreventionListener implements Listener {
    private final CheatPrevention expansion;
    private final ICombatLogX plugin;
    private final Map<String, List<UUID>> cooldownMap;
    CheatPreventionListener(CheatPrevention expansion) {
        this.expansion = Objects.requireNonNull(expansion, "expansion must not be null!");
        this.plugin = this.expansion.getPlugin();
        this.cooldownMap = new HashMap<>();
    }

    public CheatPrevention getExpansion() {
        return this.expansion;
    }

    public ICombatLogX getPlugin() {
        return this.plugin;
    }

    public JavaPlugin getJavaPlugin() {
        ICombatLogX plugin = getPlugin();
        return plugin.getPlugin();
    }

    public Logger getLogger() {
        CheatPrevention expansion = getExpansion();
        return expansion.getLogger();
    }

    public FileConfiguration getConfig() {
        CheatPrevention expansion = getExpansion();
        return expansion.getConfig("cheat-prevention.yml");
    }

    public String getMessage(String key) {
        ICombatLogX plugin = getPlugin();
        ILanguageManager languageManager = plugin.getCombatLogXLanguageManager();
        return languageManager.getMessageColoredWithPrefix(key);
    }

    public void sendMessage(CommandSender sender, String... messageArray) {
        ICombatLogX plugin = getPlugin();
        ILanguageManager languageManager = plugin.getCombatLogXLanguageManager();
        languageManager.sendMessage(sender, messageArray);
    }

    public void sendMessageWithCooldown(Player player, String key) {
        if(player == null || key == null || key.isEmpty()) return;
        List<UUID> cooldownList = this.cooldownMap.getOrDefault(key, new ArrayList<>());

        UUID uuid = player.getUniqueId();
        if(cooldownList.contains(uuid)) return;
        cooldownList.add(uuid);
        this.cooldownMap.put(key, cooldownList);

        String message = getMessage(key);
        sendMessage(player, message);

        JavaPlugin plugin = getJavaPlugin();
        FileConfiguration config = getConfig();
        long messageCooldown = config.getLong("message-cooldown");
        long messageCooldownDelay = (messageCooldown * 20L);

        Runnable task = () -> removeCooldown(player, key);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLaterAsynchronously(plugin, task, messageCooldownDelay);
    }

    private void removeCooldown(Player player, String key) {
        if(player == null || key == null || key.isEmpty()) return;
        List<UUID> cooldownList = this.cooldownMap.getOrDefault(key, new ArrayList<>());

        UUID uuid = player.getUniqueId();
        cooldownList.remove(uuid);
        this.cooldownMap.put(key, cooldownList);
    }

    public boolean isInCombat(Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        return combatManager.isInCombat(player);
    }
}

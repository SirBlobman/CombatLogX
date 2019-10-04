package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.shaded.utility.Util;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class ListenerBlocks implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    public ListenerBlocks(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("blocks.prevent-breaking")) return;

        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        e.setCancelled(true);
        sendMessageWithCooldown(player, "cheat-prevention.blocks.no-breaking");
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("blocks.prevent-placing")) return;

        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        e.setCancelled(true);
        sendMessageWithCooldown(player, "cheat-prevention.blocks.no-placing");
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("blocks.prevent-interaction")) return;

        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        e.setCancelled(true);
        sendMessageWithCooldown(player, "cheat-prevention.blocks.no-interaction");
    }

    private final Map<String, List<UUID>> messagePathToCooldownList = Util.newMap();
    private void sendMessageWithCooldown(Player player, String path) {
        if(player == null || path == null || path.isEmpty()) return;
        List<UUID> messageCooldownList = messagePathToCooldownList.getOrDefault(path, Util.newList());

        UUID uuid = player.getUniqueId();
        if(messageCooldownList.contains(uuid)) return;

        String message = this.plugin.getLanguageMessageColoredWithPrefix(path);
        this.plugin.sendMessage(player, message);
        messageCooldownList.add(uuid);
        messagePathToCooldownList.put(path, messageCooldownList);

        BukkitScheduler scheduler = Bukkit.getScheduler();
        Runnable task = () -> removeCooldown(player, path);

        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        long messageCooldown = 20L * config.getLong("message-cooldown");
        scheduler.runTaskLater(this.plugin.getPlugin(), task, messageCooldown);
    }

    private void removeCooldown(Player player, String path) {
        if(player == null || path == null || path.isEmpty()) return;
        List<UUID> messageCooldownList = messagePathToCooldownList.getOrDefault(path, Util.newList());

        UUID uuid = player.getUniqueId();
        messageCooldownList.remove(uuid);
        messagePathToCooldownList.put(path, messageCooldownList);
    }
}
package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.List;
import java.util.UUID;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.shaded.utility.Util;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class ListenerNewItemPickup implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    public ListenerNewItemPickup(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    @EventHandler(priority= EventPriority.HIGH, ignoreCancelled=true)
    public void onPickupItem(EntityPickupItemEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("items.prevent-item-pickup")) return;

        LivingEntity entity = e.getEntity();
        if(!(entity instanceof Player)) return;

        Player player = (Player) entity;
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        e.setCancelled(true);
        sendMessage(player);
    }

    private final List<UUID> messageCooldownList = Util.newList();
    private void sendMessage(Player player) {
        if(player == null) return;

        UUID uuid = player.getUniqueId();
        if(messageCooldownList.contains(uuid)) return;

        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.items.no-pickup");
        this.plugin.sendMessage(player, message);
        messageCooldownList.add(uuid);

        BukkitScheduler scheduler = Bukkit.getScheduler();
        Runnable task = () -> messageCooldownList.remove(uuid);

        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        long messageCooldown = 20L * config.getLong("message-cooldown");
        scheduler.runTaskLater(this.plugin.getPlugin(), task, messageCooldown);
    }
}
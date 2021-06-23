package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;

public class ListenerLegacyItemPickup implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    private final List<UUID> messageCooldownList;

    public ListenerLegacyItemPickup(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
        this.messageCooldownList = new ArrayList<>();
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPickupItem(PlayerPickupItemEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("items.prevent-item-pickup")) return;

        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        e.setCancelled(true);
        sendMessage(player);
    }

    private void sendMessage(Player player) {
        if(player == null) return;

        UUID uuid = player.getUniqueId();
        if(messageCooldownList.contains(uuid)) return;
        messageCooldownList.add(uuid);

        ILanguageManager languageManager = this.plugin.getCombatLogXLanguageManager();
        String message = languageManager.getMessageColoredWithPrefix("cheat-prevention.items.no-pickup");
        languageManager.sendMessage(player, message);

        JavaPlugin plugin = this.plugin.getPlugin();
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        long messageCooldown = config.getLong("message-cooldown");
        long messageDelay = (messageCooldown * 20L);

        Runnable task = () -> messageCooldownList.remove(uuid);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, task, messageDelay);
    }
}

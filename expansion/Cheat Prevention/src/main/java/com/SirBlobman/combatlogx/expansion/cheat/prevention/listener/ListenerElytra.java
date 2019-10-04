package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class ListenerElytra implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    public ListenerElytra(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = expansion.getPlugin();
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onTimerChange(PlayerCombatTimerChangeEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("items.prevent-elytra")) return;

        Player player = e.getPlayer();
        if(!player.isGliding()) return;

        player.setGliding(false);
        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.elytra.force-disabled");
        this.plugin.sendMessage(player, message);
    }

    @EventHandler(priority= EventPriority.HIGH, ignoreCancelled=true)
    public void onToggleGlide(EntityToggleGlideEvent e) {
        if(!e.isGliding()) return;

        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("items.prevent-elytra")) return;

        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;

        Player player = (Player) entity;
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        e.setCancelled(true);
        player.setGliding(false);

        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.elytra.no-gliding");
        this.plugin.sendMessage(player, message);
    }
}
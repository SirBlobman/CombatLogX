package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;

public class ListenerTotemOfUndying implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    public ListenerTotemOfUndying(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = expansion.getPlugin();
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onResurrect(EntityResurrectEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("items.prevent-totem-usage")) return;

        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;

        Player player = (Player) entity;
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        e.setCancelled(true);
        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.no-totem");
        this.plugin.sendMessage(player, message);
    }
}
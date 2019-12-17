package com.SirBlobman.combatlogx.expansion.compatibility.mvdwplaceholderapi.hook;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.expansion.compatibility.mvdwplaceholderapi.CompatibilityMVdWPlaceholderAPI;
import com.SirBlobman.combatlogx.utility.PlaceholderReplacer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class HookMVdW implements be.maximvdw.placeholderapi.PlaceholderReplacer {
    private final ICombatLogX plugin;
    public HookMVdW(CompatibilityMVdWPlaceholderAPI expansion) {
        this.plugin = expansion.getPlugin();
    }

    private static final List<String> placeholderList = Arrays.asList("time_left", "in_combat", "status", "enemy_name", "enemy_health", "enemy_health_rounded", "enemy_hearts");
    public void register() {
        String prefix = "combatlogx_";
        JavaPlugin plugin = this.plugin.getPlugin();
        for(String string : placeholderList) {
            String placeholder = (prefix + string);
            PlaceholderAPI.registerPlaceholder(plugin, placeholder, this);
        }
    }

    @Override
    public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
        Player player = e.getPlayer();
        if(player == null) return null;

        String placeholder = e.getPlaceholder();
        if(!placeholder.startsWith("combatlogx_")) return null;

        String id = placeholder.substring("combatlogx_".length());
        switch(id) {
            case "time_left": return PlaceholderReplacer.getTimeLeftSeconds(this.plugin, player);
            case "in_combat": return PlaceholderReplacer.getInCombat(this.plugin, player);
            case "status": return PlaceholderReplacer.getCombatStatus(this.plugin, player);
            case "enemy_name": return PlaceholderReplacer.getEnemyName(this.plugin, player);
            case "enemy_health": return PlaceholderReplacer.getEnemyHealth(this.plugin, player);
            case "enemy_health_rounded": return PlaceholderReplacer.getEnemyHealthRounded(this.plugin, player);
            case "enemy_hearts": return PlaceholderReplacer.getEnemyHearts(this.plugin, player);

            default: return null;
        }
    }
}
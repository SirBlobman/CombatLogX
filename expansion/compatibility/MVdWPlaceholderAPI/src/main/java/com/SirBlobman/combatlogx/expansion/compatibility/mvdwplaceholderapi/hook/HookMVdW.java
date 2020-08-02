package com.SirBlobman.combatlogx.expansion.compatibility.mvdwplaceholderapi.hook;

import java.util.Arrays;
import java.util.List;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.expansion.compatibility.mvdwplaceholderapi.CompatibilityMVdWPlaceholderAPI;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

import static com.SirBlobman.combatlogx.utility.PlaceholderReplacer.*;

public class HookMVdW implements PlaceholderReplacer {
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
            case "time_left": return getTimeLeftSeconds(this.plugin, player);
            case "in_combat": return getInCombat(this.plugin, player);
            case "status": return getCombatStatus(this.plugin, player);
            case "enemy_name": return getEnemyName(this.plugin, player);
            case "enemy_health": return getEnemyHealth(this.plugin, player);
            case "enemy_health_rounded": return getEnemyHealthRounded(this.plugin, player);
            case "enemy_hearts": return getEnemyHearts(this.plugin, player);

            default: break;
        }
        
        return null;
    }
}
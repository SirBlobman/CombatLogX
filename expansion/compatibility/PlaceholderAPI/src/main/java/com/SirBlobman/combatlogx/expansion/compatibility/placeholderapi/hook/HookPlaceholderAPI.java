package com.SirBlobman.combatlogx.expansion.compatibility.placeholderapi.hook;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.expansion.compatibility.placeholderapi.CompatibilityPlaceholderAPI;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import static com.SirBlobman.combatlogx.utility.PlaceholderReplacer.*;

public class HookPlaceholderAPI extends PlaceholderExpansion {
    private final CompatibilityPlaceholderAPI expansion;
    private final ICombatLogX plugin;
    public HookPlaceholderAPI(CompatibilityPlaceholderAPI expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public boolean canRegister() {
        return true;
    }
    
    @Override
    public String getAuthor() {
        return "SirBlobman";
    }
    
    @Override
    public String getIdentifier() {
        return "combatlogx";
    }
    
    @Override
    public String getVersion() {
        return this.expansion.getDescription().getVersion();
    }
    
    @Override
    public String onPlaceholderRequest(Player player, String id) {
        if(player == null) return null;
        
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
package com.SirBlobman.combatlogx.expansion.compatibility.placeholderapi.hook;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.expansion.compatibility.placeholderapi.CompatibilityPlaceholderAPI;
import com.SirBlobman.combatlogx.utility.PlaceholderReplacer;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

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
        return this.expansion.getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String id) {
        if(player == null) return null;

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
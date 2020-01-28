package com.SirBlobman.combatlogx.expansion.compatibility.lands.hook;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.expansion.compatibility.lands.CompatibilityLands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.LandArea;
import me.angeschossen.lands.api.role.enums.RoleSetting;

public class HookLands {
    private final ICombatLogX plugin;
    public HookLands(CompatibilityLands expansion) {
        this.plugin = expansion.getPlugin();
    }

    private LandsIntegration integration;
    private String disableKey;
    private LandsIntegration getAPI() {
        if(this.integration == null) {
            JavaPlugin plugin = this.plugin.getPlugin();
            this.integration = new LandsIntegration(plugin, false);
        }

        if(this.disableKey == null) this.disableKey = this.integration.initialize();

        return this.integration;
    }

    public boolean isSafeZone(Player player, Location location) {
        LandsIntegration api = getAPI();
        if(api == null) return false;
    
        LandArea area = api.getArea(location);
        if(area == null) return false;
        
        return !area.canSetting(player, RoleSetting.ATTACK_PLAYER, false);
    }
}
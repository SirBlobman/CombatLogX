package com.SirBlobman.combatlogx.expansion.compatibility.lands.hook;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.expansion.compatibility.lands.CompatibilityLands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.role.enums.RoleSetting;

public class HookLands {
    private final LandsIntegration landsIntegration;
    public HookLands(CompatibilityLands expansion) {
        ICombatLogX clx = expansion.getPlugin();
        JavaPlugin plugin = clx.getPlugin();
        this.landsIntegration = new LandsIntegration(plugin);
    }
    
    private LandsIntegration getAPI() {
        return this.landsIntegration;
    }

    public boolean isSafeZone(Player player, Location location) {
        Area area = this.landsIntegration.getAreaByLoc(location);
        return (area != null && !area.canSetting(player, RoleSetting.ATTACK_PLAYER, false));
    }
}
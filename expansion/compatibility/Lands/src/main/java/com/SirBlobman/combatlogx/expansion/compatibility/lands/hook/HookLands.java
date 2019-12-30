package com.SirBlobman.combatlogx.expansion.compatibility.lands.hook;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.expansion.compatibility.lands.CompatibilityLands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.LandChunk;
import me.angeschossen.lands.api.role.enums.RoleSetting;

public class HookLands {
    private final CompatibilityLands expansion;
    private final ICombatLogX plugin;
    public HookLands(CompatibilityLands expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
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

        LandChunk chunk = api.getLandChunk(location);
        if(chunk == null) return false;

        return !chunk.canAction(player, RoleSetting.ATTACK_PLAYER, true);
    }
}
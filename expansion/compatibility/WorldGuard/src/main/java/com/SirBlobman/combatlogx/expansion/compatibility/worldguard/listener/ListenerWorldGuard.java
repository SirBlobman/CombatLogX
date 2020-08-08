package com.SirBlobman.combatlogx.expansion.compatibility.worldguard.listener;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.CompatibilityWorldGuard;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard;

public class ListenerWorldGuard implements Listener {
    private final CompatibilityWorldGuard expansion;
    public ListenerWorldGuard(CompatibilityWorldGuard expansion) {
        this.expansion = Objects.requireNonNull(expansion, "expansion must not be null!");
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        LivingEntity enemy = e.getEnemy();

        ICombatManager combatManager = this.expansion.getPlugin().getCombatManager();
        boolean preventTag = (combatManager.isInCombat(player) ? shouldPreventReTag(location, enemy) : shouldPreventTag(location, enemy));
        if(preventTag) e.setCancelled(true);
    }

    private boolean shouldPreventTag(Location location, LivingEntity enemy) {
        if(!HookWorldGuard.allowsTagging(location)) return true;
        if(enemy instanceof Player ? HookWorldGuard.allowsPVP(location) : HookWorldGuard.allowsMobCombat(location)) return false;

        FileConfiguration config = this.expansion.getConfig("worldguard-compatibility.yml");
        return !config.getBoolean("allow-tag-in-safezone");
    }

    private boolean shouldPreventReTag(Location location, LivingEntity enemy) {
        if(!HookWorldGuard.allowsTagging(location)) return true;
        if(enemy instanceof Player ? HookWorldGuard.allowsPVP(location) : HookWorldGuard.allowsMobCombat(location)) return false;

        FileConfiguration config = this.expansion.getConfig("worldguard-compatibility.yml");
        return !config.getBoolean("allow-retag-in-safezone");
    }
}
package com.SirBlobman.combatlogx.expansion.compatibility.citizens.utility;

import java.util.UUID;

import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import net.citizensnpcs.api.npc.NPC;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mcmonkey.sentinel.targeting.SentinelTargetLabel;

public final class SentinelManager {
    public static void setSentinelOptions(CompatibilityCitizens expansion, NPC npc, Player player, LivingEntity enemy) {
        if(expansion == null || npc == null || player == null) return;

        PluginManager manager = Bukkit.getPluginManager();
        FileConfiguration config = expansion.getConfig("citizens-compatibility.yml");
        boolean enabled = manager.isPluginEnabled("Sentinel") && config.getBoolean("sentinel-options.enable-sentinel");
        if(!enabled) return;

        SentinelTrait sentinel = npc.getTrait(SentinelTrait.class);
        sentinel.setInvincible(false);
        sentinel.respawnTime = -1L;

        if(enemy != null) {
            boolean attackFirst = config.getBoolean("sentinel-options.attack-first");
            if(attackFirst) setAttackFirst(sentinel, enemy);
        }
    }

    private static void setAttackFirst(SentinelTrait sentinel, LivingEntity enemy) {
        if(sentinel == null || enemy == null) return;

        UUID uuid = enemy.getUniqueId();
        String uuidString = uuid.toString();

        SentinelTargetLabel label = new SentinelTargetLabel("uuid:" + uuidString);
        label.addToList(sentinel.allTargets);
    }
}
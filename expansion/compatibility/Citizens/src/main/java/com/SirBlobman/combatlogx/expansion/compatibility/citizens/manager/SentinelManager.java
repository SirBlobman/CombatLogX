package com.SirBlobman.combatlogx.expansion.compatibility.citizens.manager;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;

import net.citizensnpcs.api.npc.NPC;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mcmonkey.sentinel.targeting.SentinelTargetLabel;

public class SentinelManager {
    private final CompatibilityCitizens expansion;
    public SentinelManager(CompatibilityCitizens expansion) {
        this.expansion = Objects.requireNonNull(expansion, "expansion must not be null!");
    }
    
    public void onEnable() {
        // Do Nothing
    }
    
    protected void setOptions(NPC npc, Player player, LivingEntity enemy) {
        if(npc == null || player == null) return;
        
        SentinelTrait sentinelTrait = npc.getTrait(SentinelTrait.class);
        sentinelTrait.setInvincible(false);
        sentinelTrait.respawnTime = -1L;
        
        if(enemy != null) {
            FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
            boolean attackFirst = config.getBoolean("sentinel-options.attack-first", false);
            if(attackFirst) setAttackFirst(sentinelTrait, enemy);
        }
    }
    
    private void setAttackFirst(SentinelTrait trait, LivingEntity enemy) {
        if(trait == null || enemy == null) return;
        
        UUID uuid = enemy.getUniqueId();
        String uuidString = uuid.toString();
        
        SentinelTargetLabel label = new SentinelTargetLabel("uuid:" + uuidString);
        label.addToList(trait.allTargets);
    }
}

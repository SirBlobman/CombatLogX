package com.SirBlobman.expansion.citizens.utility;

import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.citizens.config.ConfigCitizens;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mcmonkey.sentinel.targeting.SentinelTargetLabel;

import java.util.UUID;

public class SentinelUtil {
    public static void setSentinel(NPC npc, Player player, LivingEntity enemy) {
        boolean sentinel = PluginUtil.isEnabled("Sentinel", "mcmonkey") && ConfigCitizens.getOption("citizens.sentinel.use sentinel", true);
        if(!sentinel) return;

        SentinelTrait sentinelTrait = npc.getTrait(SentinelTrait.class);
        sentinelTrait.setInvincible(false);
        sentinelTrait.respawnTime = -1L;

        if(enemy != null) {
            boolean attackFirst = ConfigCitizens.getOption("citizens.sentinel.attack first", false);
            if(attackFirst) enableAttackFirst(sentinelTrait, enemy);
        }
    }

    private static void enableAttackFirst(SentinelTrait trait, LivingEntity enemy) {
        if(trait == null || enemy == null) return;

        UUID uuid = enemy.getUniqueId();
        String uuidString = uuid.toString();
        SentinelTargetLabel label = new SentinelTargetLabel("uuid:" + uuidString);
        label.addToList(trait.allTargets);
    }
}

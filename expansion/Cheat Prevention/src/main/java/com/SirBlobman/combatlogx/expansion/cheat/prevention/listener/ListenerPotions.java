package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.Collection;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerPotions extends CheatPreventionListener {
    public ListenerPotions(CheatPrevention expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        Collection<PotionEffect> potionEffectList = player.getActivePotionEffects();
        for(PotionEffect potionEffect : potionEffectList) {
            if(potionEffect == null) continue;

            PotionEffectType potionEffectType = potionEffect.getType();
            if(isBlocked(potionEffectType)) player.removePotionEffect(potionEffectType);
        }
    }

    private boolean isBlocked(PotionEffectType type) {
        FileConfiguration config = getConfig();
        List<String> blockedPotionList = config.getStringList("blocked-potion-list");

        String typeName = type.getName();
        return blockedPotionList.contains(typeName);
    }
}

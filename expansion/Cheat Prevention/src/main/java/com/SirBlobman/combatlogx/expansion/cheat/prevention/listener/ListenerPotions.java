package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.Collection;
import java.util.List;

import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ListenerPotions implements Listener {
    private final Expansion expansion;
    public ListenerPotions(Expansion expansion) {
        this.expansion = expansion;
    }

    private boolean isBlocked(PotionEffectType type) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        List<String> blockedPotionList = config.getStringList("blocked-potion-list");

        String typeName = type.getName();
        return blockedPotionList.contains(typeName);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        Collection<PotionEffect> potionEffectList = player.getActivePotionEffects();
        for(PotionEffect potionEffect : potionEffectList) {
            if(potionEffect == null) continue;

            PotionEffectType type = potionEffect.getType();
            if(isBlocked(type)) player.removePotionEffect(type);
        }
    }
}

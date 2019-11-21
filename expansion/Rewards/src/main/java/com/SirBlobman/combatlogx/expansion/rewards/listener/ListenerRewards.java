package com.SirBlobman.combatlogx.expansion.rewards.listener;

import java.util.List;

import com.SirBlobman.combatlogx.expansion.rewards.Rewards;
import com.SirBlobman.combatlogx.expansion.rewards.object.Reward;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class ListenerRewards implements Listener {
    private final Rewards expansion;
    public ListenerRewards(Rewards expansion) {
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        Player killer = entity.getKiller();
        if(killer == null) return;

        List<Reward> rewardList = this.expansion.getAllRewards();
        for(Reward reward : rewardList) reward.tryActivate(killer, entity);
    }
}
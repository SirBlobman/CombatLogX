package com.SirBlobman.expansion.rewards;

import java.io.File;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.expansion.rewards.config.ConfigRewards;
import com.SirBlobman.expansion.rewards.config.Reward;

public class Rewards implements CLXExpansion, Listener {
    public String getUnlocalizedName() {return "Rewards";}
    public String getVersion() {return "13.3";}
    
    public static File FOLDER;
    
    @Override
    public void enable() {
        FOLDER = getDataFolder();
        ConfigRewards.load();
        PluginUtil.regEvents(this);
    }
    
    @Override
    public void disable() {
        
    }
    
    @Override
    public void onConfigReload() {
        FOLDER = getDataFolder();
        ConfigRewards.getRewards(true);
    }
    
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        final LivingEntity le = e.getEntity();
        final Player killer = le.getKiller();
        if(killer != null) {
            LivingEntity enemyOfKiller = CombatUtil.getEnemy(killer);
            if(le.equals(enemyOfKiller)) SchedulerUtil.runNowAsync(() -> {
                List<Reward> rewards = ConfigRewards.getRewards(false);
                rewards.forEach(reward -> {
                    if(reward.canTriggerReward(killer, le)) reward.triggerReward(killer, le);
                });
            });
        }
    }
}
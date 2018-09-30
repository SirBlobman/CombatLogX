package com.SirBlobman.expansion.rewards;

import java.io.File;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.rewards.config.ConfigRewards;

public class Rewards implements CLXExpansion, Listener {
    public String getUnlocalizedName() {return "Rewards";}
    public String getVersion() {return "13.2";}
    
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
        ConfigRewards.load();
    }
    
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity le = e.getEntity();
        List<LivingEntity> enemies = CombatUtil.getLinkedEnemies();
        if(enemies.contains(le)) {
            OfflinePlayer enemy = CombatUtil.getByEnemy(le);
            Player killer = le.getKiller();
            if(killer != null && killer.equals(enemy)) {
                ConfigRewards.getRewards(false).forEach(reward -> {
                    if(reward.canTriggerReward(killer, le)) reward.triggerReward(killer, le);
                });
            }
        }
    }
}
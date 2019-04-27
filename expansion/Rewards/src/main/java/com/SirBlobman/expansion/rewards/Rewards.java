package com.SirBlobman.expansion.rewards;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.expansion.rewards.config.ConfigRewards;
import com.SirBlobman.expansion.rewards.config.Reward;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.File;
import java.util.List;

public class Rewards implements CLXExpansion, Listener {
    public static File FOLDER;

    public String getUnlocalizedName() {
        return "Rewards";
    }

    public String getVersion() {
        return "13.5";
    }

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
        LivingEntity killed = e.getEntity();
        if(killed == null) return;
        
        Player killer = killed.getKiller();
        if(killer == null) return;
        
        LivingEntity killerEnemy = CombatUtil.getEnemy(killer);
        if(killerEnemy == null) return;
        if(!killed.equals(killerEnemy)) return;
        
        SchedulerUtil.runNowAsync(() -> {
            List<Reward> rewardList = ConfigRewards.getRewards(false);
            for(Reward reward : rewardList) {
                if(!reward.canTriggerReward(killer, killed)) continue;
                reward.triggerReward(killer, killed);
            }
        });
    }
}
package com.SirBlobman.expansion.rewards;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.expansion.rewards.config.ConfigRewards;
import com.SirBlobman.expansion.rewards.config.Reward;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
        return "14.4";
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

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onDeath(EntityDeathEvent e) {
        LivingEntity killed = e.getEntity();
        if(killed == null) return;
        
        Player killer = killed.getKiller();
        if(killer == null) return;

        World world = killer.getWorld();
        String worldName = world.getName();
        if(ConfigOptions.OPTION_DISABLED_WORLDS.contains(worldName)) return;
        
        SchedulerUtil.runNowAsync(() -> {
            List<Reward> rewardList = ConfigRewards.getRewards(false);
            for(Reward reward : rewardList) {
                if(!reward.canTriggerReward(killer, killed)) continue;
                reward.triggerReward(killer, killed);
            }
        });
    }
}
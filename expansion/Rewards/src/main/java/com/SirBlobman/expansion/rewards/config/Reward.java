package com.SirBlobman.expansion.rewards.config;

import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class Reward {
    private final List<String> validWorlds, validMobTypes, commands;
    
    Reward(List<String> validWorlds, List<String> validMobTypes, List<String> commands) {
        Validate.notEmpty(validWorlds, "validWorlds cannot be empty or NULL!");
        Validate.notEmpty(validMobTypes, "validMobTypes cannot be empty or NULL!");
        Validate.notEmpty(commands, "commands cannot be empty or NULL!");
        this.validWorlds = validWorlds;
        this.validMobTypes = validMobTypes;
        this.commands = commands;
    }
    
    public boolean failsRewardTrigger(Player player, LivingEntity killed) {
        if(player == null || killed == null) return true;
        
        if(!validWorlds.contains("*")) {
            World world = player.getWorld();
            String worldName = world.getName();
            if(!validWorlds.contains(worldName)) return true;
        }
        
        if(!validMobTypes.contains("*")) {
            EntityType mobType = killed.getType();
            String mobTypeString = mobType.name();
            return !validMobTypes.contains(mobTypeString);
        }
        
        return false;
    }
    
    public void triggerReward(Player player, LivingEntity killed) {
        if(failsRewardTrigger(player, killed)) return;
        
        CommandSender console = Bukkit.getConsoleSender();
        for(String command : this.commands) {
            String finalCommand = command.replace("{player}", player.getName()).replace("{killed}", killed.getName());
            Runnable task = () -> {
                try {Bukkit.dispatchCommand(console, finalCommand);} 
                catch(CommandException ex) {
                    Util.log("Failed to run command '" + finalCommand + "' due to an error:");
                    ex.printStackTrace();
                }
            };
            SchedulerUtil.runSync(task);
        }
    }
}
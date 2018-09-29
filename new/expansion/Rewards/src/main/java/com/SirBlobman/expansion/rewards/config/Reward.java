package com.SirBlobman.expansion.rewards.config;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.utility.CombatUtil;

public class Reward {
    private final List<String> validWorlds;
    private final List<String> commands;
    public Reward(List<String> validWorlds, List<String> commands) {
        Validate.notEmpty(validWorlds, "validWorlds cannot be empty or NULL!");
        Validate.notEmpty(commands, "commands cannot be empty or NULL!");
        this.validWorlds = validWorlds;
        this.commands = commands;
    }
    
    public boolean canTriggerReward(Player player, LivingEntity killed) {
        if(player != null && killed != null) {
            World world = player.getWorld();
            String worldName = world.getName();
            if(validWorlds.contains(worldName)) {
                LivingEntity enemy = CombatUtil.getEnemy(player);
                return killed.equals(enemy);
            } else return false;
        } else return false;
    }
    
    public void triggerReward(Player player, LivingEntity killed) {
        Validate.notNull(player, "player cannot be NULL!");
        Validate.notNull(killed, "killed cannot be NULL!");
        
        World world = player.getWorld();
        String worldName = world.getName();
        Validate.isTrue(validWorlds.contains(worldName), "Invalid world for player!");
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        Validate.isTrue(enemy.equals(killed), "Invalid killed entity for player!");
        
        commands.forEach(command -> {
            String cmd = command.replace("{player}", player.getName()).replace("{killed}", killed.getName());
            CommandSender cs = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(cs, cmd);
        });
    }
}
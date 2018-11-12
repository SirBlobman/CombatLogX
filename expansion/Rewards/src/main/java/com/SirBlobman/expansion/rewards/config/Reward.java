package com.SirBlobman.expansion.rewards.config;

import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class Reward {
    private final List<String> validWorlds;
    private final List<String> commands;

    Reward(List<String> validWorlds, List<String> commands) {
        Validate.notEmpty(validWorlds, "validWorlds cannot be empty or NULL!");
        Validate.notEmpty(commands, "commands cannot be empty or NULL!");
        this.validWorlds = validWorlds;
        this.commands = commands;
    }

    public boolean canTriggerReward(Player player, LivingEntity killed) {
        if (player != null && killed != null) {
            World world = player.getWorld();
            String worldName = world.getName();
            return (validWorlds.contains(worldName) || validWorlds.contains("*"));
        } else return false;
    }

    public void triggerReward(Player player, LivingEntity killed) {
        Validate.notNull(player, "player cannot be NULL!");
        Validate.notNull(killed, "killed cannot be NULL!");

        World world = player.getWorld();
        String worldName = world.getName();
        Validate.isTrue(validWorlds.contains(worldName) || validWorlds.contains("*"), "Invalid world for player!");

        commands.forEach(command -> {
            String cmd = command.replace("{player}", player.getName()).replace("{killed}", killed.getName());
            CommandSender cs = Bukkit.getConsoleSender();
            SchedulerUtil.runSync(() -> Bukkit.dispatchCommand(cs, cmd));
        });
    }
}
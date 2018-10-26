package com.SirBlobman.rewards;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.rewards.config.ConfigRewards;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;
import java.util.List;

public class Rewards implements CLXExpansion, Listener {
    public static File FOLDER;
    
    @Override
    public void enable() {
        FOLDER = getDataFolder();
        ConfigRewards.load();
        Util.regEvents(this);
    }
    
    public String getUnlocalizedName() {
        return "Rewards";
    }
    
    public String getName() {
        return "Rewards";
    }
    
    public String getVersion() {
        return "3";
    }
    
    @Override
    public void onConfigReload() {
        ConfigRewards.load();
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Player killer = player.getKiller();
        if (killer != null) {
            for (String s : ConfigRewards.OPTION_KILL_COMMANDS) {
                try {
                    List<String> l1 = Util.newList("{player}", "{killer}");
                    List<String> l2 = Util.newList(player.getName(), killer.getName());
                    String cmd = Util.formatMessage(s, l1, l2);
                    if (cmd.startsWith("[PLAYER]")) {
                        cmd = cmd.substring(8);
                        player.performCommand(cmd);
                    } else if (cmd.startsWith("[KILLER]")) {
                        cmd = cmd.substring(8);
                        killer.performCommand(cmd);
                    } else {
                        CommandSender cs = Util.CONSOLE;
                        Bukkit.dispatchCommand(cs, cmd);
                    }
                } catch (Throwable ex) {
                    String error = "Failed to execute command '" + s + "':";
                    print(error);
                    ex.printStackTrace();
                }
            }
        }
    }
}
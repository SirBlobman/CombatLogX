package com.SirBlobman.residence;

import java.io.File;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.residence.config.ConfigResidence;

public class CompatResidence implements CLXExpansion, Listener {
    public static File FOLDER;

    @Override
    public void enable() {
        if(PluginUtil.isPluginEnabled("Residence")) {
            FOLDER = getDataFolder();
            ConfigResidence.load();
            Util.regEvents(this);
        } else {
            String error = "Residence is not installed. This expansion is useless!";
            print(error);
        }
    }

    public String getUnlocalizedName() {return "CompatResidence";}
    public String getName() {return "Residence Compatibility";}
    public String getVersion() {return "2";}
    
    @Override
    public void onConfigReload() {
        ConfigResidence.load();
    }
    
    @EventHandler
    public void onTag(PlayerTagEvent e) {
        Player target = e.getPlayer();
        LivingEntity enemy = e.getEnemy();
        if(enemy != null && enemy instanceof Player) {
            Player attacker = (Player) enemy;
            boolean ff = ResidenceUtil.canFriendlyFire(attacker, target);
            if(!ff) e.setCancelled(true);
        }
    }
}
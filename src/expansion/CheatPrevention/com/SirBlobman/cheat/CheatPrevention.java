package com.SirBlobman.cheat;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.CombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class CheatPrevention implements CLXExpansion, Listener {
    private static List<Player> RE_ENABLE_FLIGHT = Util.newList();
    
    @Override
    public void enable() {
        Util.regEvents(this);
    }

    @Override
    public String getName() {return "Cheat Prevention";}

    @Override
    public String getVersion() {return "1.0.2";}
    
    @EventHandler
    public void ctce(CombatTimerChangeEvent e) {
        Player p = e.getPlayer();
        
        if(ConfigOptions.CHEAT_PREVENT_CHANGE_GAMEMODE) {
            String m = ConfigOptions.CHEAT_PREVENT_CHANGE_GAMEMODE_MODE;
            GameMode gm = GameMode.valueOf(m);
            p.setGameMode(gm);
        }
        
        if(ConfigOptions.CHEAT_PREVENT_DISABLE_FLIGHT) {
            if(ConfigOptions.CHEAT_PREVENT_ENABLE_FLIGHT) {
                if(p.getAllowFlight()) RE_ENABLE_FLIGHT.add(p);
            }
            
            p.setAllowFlight(false);
            p.setFlying(false);
        }
        
        for(String s : ConfigOptions.CHEAT_PREVENT_BLOCKED_POTIONS) {
            PotionEffectType pet = PotionEffectType.getByName(s);
            if(pet != null && p.hasPotionEffect(pet)) p.removePotionEffect(pet);
        }
    }
    
    @EventHandler
    public void pue(PlayerUntagEvent e) {
        Player p = e.getPlayer();
        if(ConfigOptions.CHEAT_PREVENT_ENABLE_FLIGHT) {
            if(RE_ENABLE_FLIGHT.contains(p)) {
                p.setAllowFlight(true);
                p.setFlying(true);
                RE_ENABLE_FLIGHT.remove(p);
            }
        }
    }
    
    @EventHandler
    public void ioe(InventoryOpenEvent e) {
        if(ConfigOptions.CHEAT_PREVENT_OPEN_INVENTORIES) {
            HumanEntity he = e.getPlayer();
            if(he instanceof Player) {
                Player p = (Player) he;
                if(Combat.isInCombat(p)) {
                    Inventory i = e.getInventory();
                    InventoryType it = i.getType();
                    if(it != InventoryType.PLAYER) {
                        e.setCancelled(true);
                        String msg = ConfigLang.MESSAGE_OPEN_INVENTORY;
                        Util.sendMessage(p, msg);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void tp(PlayerTeleportEvent e) {
        if(ConfigOptions.CHEAT_PREVENT_TELEPORT) {
            Player p = e.getPlayer();
            if(Combat.isInCombat(p)) {
                e.setCancelled(true);
                String msg = ConfigLang.MESSAGE_NO_TELEPORT;
                Util.sendMessage(p, msg);
            }
        }
    }
    
    @EventHandler
    public void pcpe(PlayerCommandPreprocessEvent e) {
        if(e.isCancelled()) return;
        Player p = e.getPlayer();
        if(Combat.isInCombat(p)) {
            String msg = e.getMessage();
            String cmd = msg.toLowerCase();
            boolean whitelist = ConfigOptions.CHEAT_PREVENT_BLOCKED_COMMANDS_MODE;
            List<String> list = ConfigOptions.CHEAT_PREVENT_BLOCKED_COMMANDS;
            if(!whitelist) {
                for(String blocked : list) {
                    blocked = "/" + blocked.toLowerCase();
                    if(cmd.startsWith(blocked)) {
                        e.setCancelled(true);
                        break;
                    }
                }
            } else {
                e.setCancelled(true);
                for(String allowed : list) {
                    allowed = "/" + allowed.toLowerCase();
                    if(cmd.startsWith(allowed)) {
                        e.setCancelled(false);
                        break;
                    }
                }
            }
            
            if(e.isCancelled()) {
                List<String> l1 = Util.newList("{command}");
                List<String> l2 = Util.newList(msg);
                String msg1 = Util.formatMessage(ConfigLang.MESSAGE_BLOCKED_COMMAND, l1, l2);
                Util.sendMessage(p, msg1);
            }
        }
    }
}
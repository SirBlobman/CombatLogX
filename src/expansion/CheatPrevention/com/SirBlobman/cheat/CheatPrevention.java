package com.SirBlobman.cheat;

import java.io.File;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.CombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;

public class CheatPrevention implements CLXExpansion, Listener {
    public static File FOLDER;
    private static List<Player> RE_ENABLE_FLIGHT = Util.newList();
    private static List<Player> DO_ONCE = Util.newList();
    
    @Override
    public void enable() {
        FOLDER = getDataFolder();
        ConfigCheatPrevention.load();
        Util.regEvents(this);
    }

    public String getUnlocalizedName() {return "CheatPrevention";}
    public String getName() {return "Cheat Prevention";}
    public String getVersion() {return "2";}
    
    @EventHandler
    public void ctce(CombatTimerChangeEvent e) {
        Player p = e.getPlayer();
        
        if(ConfigCheatPrevention.CHEAT_PREVENT_CHANGE_GAMEMODE) {
            String m = ConfigCheatPrevention.CHEAT_PREVENT_CHANGE_GAMEMODE_MODE;
            GameMode gm = GameMode.valueOf(m);
            p.setGameMode(gm);
        }
        
        if(ConfigCheatPrevention.CHEAT_PREVENT_DISABLE_FLIGHT) {
            if(p.getAllowFlight()) {
            	p.setFlying(false);
            	p.setAllowFlight(false);
            }
        }
        
        if(!DO_ONCE.contains(p)) {
            if(ConfigCheatPrevention.CHEAT_PREVENT_AUTO_CLOSE_GUIS) {
                p.closeInventory();
                DO_ONCE.add(p);
            }
        }
        
        for(String s : ConfigCheatPrevention.CHEAT_PREVENT_BLOCKED_POTIONS) {
            try {
                PotionEffectType pet = PotionEffectType.getByName(s);
                if(p.hasPotionEffect(pet)) p.removePotionEffect(pet);
            } catch(Throwable ex) {
                String error = "Invalid potion effect '" + s + "' in combat.yml";
                Util.print(error);
            }
        }
    }
    
    @EventHandler
    public void pue(PlayerUntagEvent e) {
        Player p = e.getPlayer();
        if(ConfigCheatPrevention.CHEAT_PREVENT_ENABLE_FLIGHT) {
            if(RE_ENABLE_FLIGHT.contains(p)) {
                p.setAllowFlight(true);
                p.setFlying(true);
                RE_ENABLE_FLIGHT.remove(p);
            }
        }
        DO_ONCE.remove(p);
    }
    
    @EventHandler
    public void ioe(InventoryOpenEvent e) {
        if(ConfigCheatPrevention.CHEAT_PREVENT_OPEN_INVENTORIES) {
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
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void toggleFlight(PlayerToggleFlightEvent e) {
    	if(e.isCancelled()) return;
    	else {
    		Player p = e.getPlayer();
    		if(ConfigCheatPrevention.CHEAT_PREVENT_DISABLE_FLIGHT) {
    			if(Combat.isInCombat(p)) {
    				if(p.getAllowFlight()) RE_ENABLE_FLIGHT.add(p);
    				p.setAllowFlight(false);
    				p.setFlying(false);
    				e.setCancelled(true);
    			} else {
    				if(ConfigCheatPrevention.CHEAT_PREVENT_ENABLE_FLIGHT && RE_ENABLE_FLIGHT.contains(p)) {
    					RE_ENABLE_FLIGHT.remove(p);
    					p.setAllowFlight(true);
    				}
    			}
    		}
    	}
    }
    
    @EventHandler
    public void tp(PlayerTeleportEvent e) {
        if(ConfigCheatPrevention.CHEAT_PREVENT_TELEPORT) {
            Player p = e.getPlayer();
            TeleportCause tc = e.getCause();
            if(tc == TeleportCause.ENDER_PEARL) {
                if(ConfigCheatPrevention.CHEAT_PREVENT_TELEPORT_ALLOW_ENDERPEARLS) {
                    if(ConfigCheatPrevention.CHEAT_PREVENT_TELEPORT_ENDERPEARLS_RESTART) {
                        Combat.tag(p, Combat.getEnemy(p));
                    }
                    return;
                }
            } else if(tc == TeleportCause.COMMAND || tc == TeleportCause.ENDER_PEARL) {
                if(Combat.isInCombat(p)) {
                    e.setCancelled(true);
                    String msg = ConfigLang.MESSAGE_NO_TELEPORT;
                    Util.sendMessage(p, msg);
                }
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
            boolean whitelist = ConfigCheatPrevention.CHEAT_PREVENT_BLOCKED_COMMANDS_MODE;
            List<String> list = ConfigCheatPrevention.CHEAT_PREVENT_BLOCKED_COMMANDS;
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
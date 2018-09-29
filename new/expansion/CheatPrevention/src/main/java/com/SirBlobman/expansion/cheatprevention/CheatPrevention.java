package com.SirBlobman.expansion.cheatprevention;

import java.io.File;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;
import com.SirBlobman.expansion.cheatprevention.olivolja3.AliasDetection;

public class CheatPrevention implements CLXExpansion, Listener {
    public String getUnlocalizedName() {return "CheatPrevention";}
    public String getName() {return "Cheat Prevention";}
    public String getVersion() {return "13.1";}
    
    public static File FOLDER;
    
    @Override
    public void enable() {
        FOLDER = getDataFolder();
        ConfigCheatPrevention.load();
        PluginUtil.regEvents(this);
        AliasDetection.cmdDetect();
    }
    
    @Override
    public void disable() {
        
    }
    
    @Override
    public void onConfigReload() {
        ConfigCheatPrevention.load();
        detectAliases();
    }
    
    @EventHandler
    public void onUntag(PlayerUntagEvent e) {
        Player p = e.getPlayer();
        UntagReason reason = e.getUntagReason();
        SchedulerUtil.runLater(5L, () -> {
            if(reason == UntagReason.EXPIRE) {
                String perm = ConfigCheatPrevention.FLIGHT_ENABLE_PERMISSION;
                if(perm != null && !perm.isEmpty()) {
                    if(p.hasPermission(perm)) {
                        p.setAllowFlight(true);
                        p.setFlying(true);
                    }
                }
            }
        });
    }
    
    @EventHandler
    public void onChangeTimer(PlayerCombatTimerChangeEvent e) {
        Player p = e.getPlayer();
        
        if(ConfigCheatPrevention.GAMEMODE_CHANGE_WHEN_TAGGED) {
            GameMode pgm = p.getGameMode();
            String smode = ConfigCheatPrevention.GAMEMODE_GAMEMODE;
            GameMode gm = GameMode.valueOf(smode);
            if(pgm != gm) {
                p.setGameMode(gm);
                List<String> keys = Util.newList("{gamemode}");
                List<?> vals = Util.newList(gm.name());
                String format = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.gamemode.change");
                String msg = Util.formatMessage(format, keys, vals);
                Util.sendMessage(p, msg);
            }
        }
        
        if(!ConfigCheatPrevention.FLIGHT_ALLOW_DURING_COMBAT) {
            if(p.isFlying() || p.getAllowFlight()) {
                p.setFlying(false);
                p.setAllowFlight(false);
                String msg = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.flight.disabled");
                Util.sendMessage(p, msg);
            }
        }
        
        if(!ConfigCheatPrevention.FLIGHT_ALLOW_ELYTRAS) {
            if(p.isGliding()) {
                p.setGliding(false);
                String msg = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.elytra.disabled");
                Util.sendMessage(p, msg);
            }
        }
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onToggleFlight(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        if(!ConfigCheatPrevention.FLIGHT_ALLOW_DURING_COMBAT && CombatUtil.isInCombat(p)) {
            if(e.isFlying()) {
                e.setCancelled(true);
                p.setAllowFlight(false);
                p.setFlying(false);
                
                String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.flight.not allowed");
                Util.sendMessage(p, error);
            }
        }
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onToggleElytra(EntityToggleGlideEvent e) {
        Entity en = e.getEntity();
        if(en instanceof Player) {
            Player p = (Player) en;
            if(!ConfigCheatPrevention.FLIGHT_ALLOW_ELYTRAS && CombatUtil.isInCombat(p)) {
                if(e.isGliding()) {
                    e.setCancelled(true);
                    p.setGliding(false);
                    
                    String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.elytra.not allowed");
                    Util.sendMessage(p, error);
                }
            }
        }
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onChangeGameMode(PlayerGameModeChangeEvent e) {
        Player p = e.getPlayer();
        if(ConfigCheatPrevention.GAMEMODE_CHANGE_WHEN_TAGGED && CombatUtil.isInCombat(p)) {
            GameMode pgm = e.getNewGameMode();
            String smode = ConfigCheatPrevention.GAMEMODE_GAMEMODE;
            GameMode gm = GameMode.valueOf(smode);
            if(pgm != gm) {
                e.setCancelled(true);
                p.setGameMode(gm);
                
                String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.gamemode.not allowed");
                Util.sendMessage(p, error);
            }
        }
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if(CombatUtil.isInCombat(p)) {
            TeleportCause cause = e.getCause();
            if(cause.equals(TeleportCause.ENDER_PEARL)) {
                if(!ConfigCheatPrevention.TELEPORTATION_ALLOW_ENDER_PEARLS) {
                    e.setCancelled(true);
                    String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.teleport.enderpearl.not allowed");
                    Util.sendMessage(p, error);
                }
                if(ConfigCheatPrevention.TELEPORTATION_ENDER_PEARLS_RESTART_TIMER) {
                    CombatUtil.tag(p, CombatUtil.getEnemy(p), TagType.PLAYER, TagReason.ATTACKED);
                }
            } else {
                if(!ConfigCheatPrevention.TELEPORTATION_ALLOW_DURING_COMBAT) {
                    e.setCancelled(true);
                    String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.teleport.other.not allowed");
                    Util.sendMessage(p, error);
                }
            }
        }
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=false)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        String[] split = message.split(" ");
        String cmd = split[0].toLowerCase();
        if(CombatUtil.isInCombat(p)) {
            if(cmd.startsWith("/cmi") && split.length > 1) {
                cmd = "/" + split[1].toLowerCase();
                if(cmd.contains(":")) {
                    String[] split1 = cmd.split(":");
                    cmd = split1[0].toLowerCase();
                }
            }
            
            List<String> commandList = Util.toLowercaseList(ConfigCheatPrevention.BLOCKED_COMMANDS_LIST);
            boolean deny = false;
            if(ConfigCheatPrevention.BLOCKED_COMMANDS_IS_WHITELIST) {
                if(!commandList.contains(cmd)) deny = true;
            } else {
                if(commandList.contains(cmd)) deny = true;
            }
            
            if(deny) {
                e.setCancelled(true);
                List<String> keys = Util.newList("{command}");
                List<?> vals = Util.newList(cmd);
                String format = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.command.not allowed");
                String error = Util.formatMessage(format, keys, vals);
                Util.sendMessage(p, error);
            }
        }
    }
    
    public static void detectAliases() {
        List<String> list = ConfigCheatPrevention.BLOCKED_COMMANDS_LIST;
        List<String> newList = Util.newList();
        
        list.forEach(blocked -> {
            String withoutSlash = blocked.substring(1);
            PluginCommand pcmd = Util.SERVER.getPluginCommand(withoutSlash);
            if(pcmd != null) {
                String withSlash = "/" + pcmd.getName();
                newList.add(withSlash);
                List<String> aliases = pcmd.getAliases();
                aliases.forEach(alias -> {
                    String asCmd = "/" + alias;
                    newList.add(asCmd);
                });
            }
        });
        
        ConfigCheatPrevention.BLOCKED_COMMANDS_LIST.addAll(newList);
    }
}
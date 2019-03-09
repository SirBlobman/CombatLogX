package com.SirBlobman.expansion.cheatprevention.listener;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.potion.PotionEffectType;

public class ListenCheatPrevention implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled=true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        String command = e.getMessage();
        String actualCommand = convertCommand(command);
        if(!isBlocked(actualCommand)) return;
        
        e.setCancelled(true);
        String format = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.command.not allowed");
        String message = format.replace("{command}", actualCommand);
        Util.sendMessage(player, message);
    }
    
    private String convertCommand(String original) {
        if(original == null || original.isEmpty()) original = "";
        if(!original.startsWith("/")) original = "/" + original;
        return original;
    }
    
    private String getMainCommand(String original) {
        if(original == null || original.isEmpty()) return "";
        
        int firstSpace = original.indexOf('\u0020');
        if(firstSpace < 0) return original;
        
        return original.substring(0, firstSpace);
    }
    
    private boolean isBlocked(String command) {
        String mainCommand = getMainCommand(command);
        
        if(ConfigCheatPrevention.BLOCKED_COMMANDS_IS_WHITELIST) {
            List<String> allowedCommands = ConfigCheatPrevention.BLOCKED_COMMANDS_LIST;
            return (!allowedCommands.contains(mainCommand) && !allowedCommands.contains(command));
        }
        
        List<String> blockedCommands = ConfigCheatPrevention.BLOCKED_COMMANDS_LIST;
        return (blockedCommands.contains(mainCommand) || blockedCommands.contains(command));
    }
    
    
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        UntagReason reason = e.getUntagReason();
        if(reason != UntagReason.EXPIRE) return;
        
        String permission = ConfigCheatPrevention.FLIGHT_ENABLE_PERMISSION;
        if(permission == null || permission.isEmpty()) return;
        if(!player.hasPermission(permission)) return;
        
        SchedulerUtil.runLater(5L, () -> {
            player.setAllowFlight(true);
            player.setFlying(true);
        });
    }
    
    
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onChangeTimer(PlayerCombatTimerChangeEvent e) {
        Player player = e.getPlayer();
        
        if(ConfigCheatPrevention.GAMEMODE_CHANGE_WHEN_TAGGED) checkGameMode(player);
        if(!ConfigCheatPrevention.FLIGHT_ALLOW_DURING_COMBAT) checkFlight(player);
        if(!ConfigCheatPrevention.BLOCKED_POTIONS.isEmpty()) checkPotions(player);
    }
    
    public void checkGameMode(Player player) {
        GameMode playerGM = player.getGameMode();
        String configStringGM = ConfigCheatPrevention.GAMEMODE_GAMEMODE;
        GameMode configGM = GameMode.valueOf(configStringGM);
        if(playerGM == configGM) return;
        
        player.setGameMode(configGM);
        String format = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.gamemode.change");
        String message = format.replace("{gramemode}", configGM.name());
        Util.sendMessage(player, message);
    }
    
    public void checkFlight(Player player) {
        if(!player.isFlying() && !player.getAllowFlight()) return;
        
        player.setFlying(false);
        player.setAllowFlight(false);
        String message = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.flight.disabled");
        Util.sendMessage(player, message);
    }
    
    public void checkPotions(Player player) {
        List<String> potionTypeList = ConfigCheatPrevention.BLOCKED_POTIONS;
        for(String potionType : potionTypeList) {
            PotionEffectType potion = PotionEffectType.getByName(potionType);
            if(potion == null) {
                Util.print("[CheatPrevention] Invalid Potion in config '" + potionType + "'.");
                continue;
            }
            
            player.removePotionEffect(potion);
        }
    }
    
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onToggleFlight(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();
        if (!ConfigCheatPrevention.FLIGHT_ALLOW_DURING_COMBAT && CombatUtil.isInCombat(player)) {
            if (e.isFlying()) {
                e.setCancelled(true);
                player.setAllowFlight(false);
                player.setFlying(false);
                
                String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.flight.not allowed");
                Util.sendMessage(player, error);
            }
        }
    }
    
    
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChangeGameMode(PlayerGameModeChangeEvent e) {
        Player player = e.getPlayer();
        if (ConfigCheatPrevention.GAMEMODE_CHANGE_WHEN_TAGGED && CombatUtil.isInCombat(player)) {
            GameMode pgm = e.getNewGameMode();
            String smode = ConfigCheatPrevention.GAMEMODE_GAMEMODE;
            GameMode gm = GameMode.valueOf(smode);
            if (pgm != gm) {
                e.setCancelled(true);
                player.setGameMode(gm);
                
                String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.gamemode.not allowed");
                Util.sendMessage(player, error);
            }
        }
    }
    
    
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if (CombatUtil.isInCombat(player)) {
            TeleportCause cause = e.getCause();
            String causeName = cause.name();
            if (!ConfigCheatPrevention.TELEPORTATION_ALLOW_DURING_COMBAT) {
                if(!ConfigCheatPrevention.TELEPORTATION_ALLOWED_CAUSES.contains(causeName)) {
                    e.setCancelled(true);
                    String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.teleport.other.not allowed");
                    Util.sendMessage(player, error);
                }
            }
            
            if(cause == TeleportCause.ENDER_PEARL && ConfigCheatPrevention.TELEPORTATION_ENDER_PEARLS_RESTART_TIMER) {
                LivingEntity enemy = CombatUtil.getEnemy(player);
                CombatUtil.tag(player, enemy, TagType.UNKNOWN, TagReason.UNKNOWN);
            }
        }
    }
    
    
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        if (ConfigCheatPrevention.INVENTORY_CLOSE_ON_COMBAT) {
            InventoryView playerView = player.getOpenInventory();
            if(playerView != null) {
                Inventory top = playerView.getTopInventory();
                if(top != null) {
                    player.closeInventory();
                    String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.inventory.closed");
                    Util.sendMessage(player, error);
                }
            }
        }
    }
    
    
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onOpenInventory(InventoryOpenEvent e) {
        HumanEntity he = e.getPlayer();
        if (he instanceof Player) {
            Player player = (Player) he;
            if (CombatUtil.isInCombat(player) && ConfigCheatPrevention.INVENTORY_PREVENT_OPENING) {
                e.setCancelled(true);
                String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.inventory.not allowed");
                Util.sendMessage(player, error);
            }
        }
    }
    
    
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (CombatUtil.isInCombat(player) && !ConfigCheatPrevention.CHAT_ALLOW_DURING_COMBAT) {
            e.setCancelled(true);
            
            String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.chat.not allowed");
            Util.sendMessage(player, error);
        }
    }
}
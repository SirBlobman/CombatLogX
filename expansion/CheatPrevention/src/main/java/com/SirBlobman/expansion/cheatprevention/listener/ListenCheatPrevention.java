package com.SirBlobman.expansion.cheatprevention.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.potion.PotionEffectType;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

import java.util.List;
import java.util.UUID;

public class ListenCheatPrevention implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onChangeTimer(PlayerCombatTimerChangeEvent e) {
        int timer = e.getSecondsLeft();
        if(timer <= 0) return;
        
        Player player = e.getPlayer();
        if(ConfigCheatPrevention.GAMEMODE_CHANGE_WHEN_TAGGED) checkGameMode(player);
        if(!ConfigCheatPrevention.FLIGHT_ALLOW_DURING_COMBAT) ListenFlight.checkFlight(player);
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
    
    public boolean canTeleport(TeleportCause cause) {
        if(ConfigCheatPrevention.TELEPORTATION_ALLOW_DURING_COMBAT) return true;
        if(cause == null) return false;
        
        String causeName = cause.name();
        List<String> allowedCauses = ConfigCheatPrevention.TELEPORTATION_ALLOWED_CAUSES;
        return allowedCauses.contains(causeName);
    }
    
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChangeGameMode(PlayerGameModeChangeEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        if(!ConfigCheatPrevention.GAMEMODE_CHANGE_WHEN_TAGGED) return;
        
        GameMode playerGameMode = e.getNewGameMode();
        String gameModeString = ConfigCheatPrevention.GAMEMODE_GAMEMODE;
        GameMode gameMode = GameMode.valueOf(gameModeString);
        if(gameMode == playerGameMode) return;
        
        e.setCancelled(true);
        player.setGameMode(gameMode);
        
        String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.gamemode.not allowed");
        Util.sendMessage(player, error);
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        TeleportCause cause = e.getCause();
        if(!canTeleport(cause)) {
            e.setCancelled(true);
            String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.teleport.other.not allowed");
            Util.sendMessage(player, error);
        }
        
        if(cause == TeleportCause.ENDER_PEARL && ConfigCheatPrevention.TELEPORTATION_ENDER_PEARLS_RESTART_TIMER) {
            LivingEntity enemy = CombatUtil.getEnemy(player);
            CombatUtil.tag(player, enemy, TagType.UNKNOWN, TagReason.UNKNOWN);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        if(!ConfigCheatPrevention.INVENTORY_CLOSE_ON_COMBAT) return;
        
        Player player = e.getPlayer();
        InventoryView playerView = player.getOpenInventory();
        if(playerView == null) return;
        
        Inventory inventory = playerView.getTopInventory();
        if(inventory == null) return;
        
        player.closeInventory();
        String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.inventory.closed");
        Util.sendMessage(player, error);
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onOpenInventory(InventoryOpenEvent e) {
        if(!ConfigCheatPrevention.INVENTORY_PREVENT_OPENING) return;
        
        HumanEntity human = e.getPlayer();
        if(!(human instanceof Player)) return;
        
        Player player = (Player) human;
        if(!CombatUtil.isInCombat(player)) return;
        
        e.setCancelled(true);
        String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.inventory.not allowed");
        Util.sendMessage(player, error);
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        if(ConfigCheatPrevention.CHAT_ALLOW_DURING_COMBAT) return;
        
        Player player = e.getPlayer();
        if (!CombatUtil.isInCombat(player)) return;
        
        e.setCancelled(true);
        String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.chat.not allowed");
        Util.sendMessage(player, error);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlaceBlock(BlockPlaceEvent e) {
        if(ConfigCheatPrevention.BLOCK_PLACING_DURING_COMBAT) return;
        
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        e.setCancelled(true);
        String message = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.blocks.placing not allowed");
        sendMessage(player, message);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onBreakBlock(BlockBreakEvent e) {
        if(ConfigCheatPrevention.BLOCK_BREAKING_DURING_COMBAT) return;
        
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        e.setCancelled(true);
        String message = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.blocks.breaking not allowed");
        sendMessage(player, message);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onDropItem(PlayerDropItemEvent e) {
        if(ConfigCheatPrevention.ITEM_DROPPING_DURING_COMBAT) return;
        
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        e.setCancelled(true);
        String message = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.items.dropping not allowed");
        sendMessage(player, message);
    }
    
    private final List<UUID> MESSAGE_COOLDOWN = Util.newList();
    private void sendMessage(Player player, String message) {
        UUID uuid = player.getUniqueId();
        if(!MESSAGE_COOLDOWN.contains(uuid)) {
            Util.sendMessage(player, message);
            
            MESSAGE_COOLDOWN.add(uuid);
            SchedulerUtil.runLater(20L * 10L, () -> MESSAGE_COOLDOWN.remove(uuid));
        }
    }
}
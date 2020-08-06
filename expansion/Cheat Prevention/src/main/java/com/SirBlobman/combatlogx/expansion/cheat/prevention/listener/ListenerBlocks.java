package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerBlocks extends CheatPreventionListener {
    public ListenerBlocks(CheatPrevention expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent e) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("blocks.prevent-breaking")) return;

        Player player = e.getPlayer();
        if(!isInCombat(player)) return;
        
        Block block = e.getBlock();
        Material blockType = block.getType();
        if(canBreak(blockType)) return;

        e.setCancelled(true);
        sendMessageWithCooldown(player, "cheat-prevention.blocks.no-breaking");
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent e) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("blocks.prevent-placing")) return;

        Player player = e.getPlayer();
        if(!isInCombat(player)) return;
    
        Block block = e.getBlock();
        Material blockType = block.getType();
        if(canPlace(blockType)) return;

        e.setCancelled(true);
        sendMessageWithCooldown(player, "cheat-prevention.blocks.no-placing");
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        if(action != Action.RIGHT_CLICK_BLOCK) return;

        FileConfiguration config = getConfig();
        if(!config.getBoolean("blocks.prevent-interaction")) return;

        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        e.setCancelled(true);
        sendMessageWithCooldown(player, "cheat-prevention.blocks.no-interaction");
    }
    
    private boolean canBreak(Material blockType) {
        FileConfiguration config = getConfig();
        List<String> noBreakList = config.getStringList("blocks.prevent-breaking-list");
        if(noBreakList.contains("*")) return false;
        
        String blockTypeName = blockType.name();
        return !noBreakList.contains(blockTypeName);
    }
    
    private boolean canPlace(Material blockType) {
        FileConfiguration config = getConfig();
        List<String> noPlaceList = config.getStringList("blocks.prevent-placing-list");
        if(noPlaceList.contains("*")) return false;
        
        String blockTypeName = blockType.name();
        return !noPlaceList.contains(blockTypeName);
    }
}
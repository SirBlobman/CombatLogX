package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;

import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerInventories extends CheatPreventionListener {
    public ListenerInventories(CheatPrevention expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("inventories.close-on-tag")) return;

        Player player = e.getPlayer();
        player.closeInventory();

        String message = getMessage("cheat-prevention.inventory.force-closed");
        sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onOpenInventory(InventoryOpenEvent e) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("inventories.prevent-opening")) return;

        HumanEntity human = e.getPlayer();
        if(!(human instanceof Player)) return;

        Player player = (Player) human;
        if(!isInCombat(player)) return;

        e.setCancelled(true);
        String message = getMessage("cheat-prevention.inventory.no-opening");
        sendMessage(player, message);
    }
}
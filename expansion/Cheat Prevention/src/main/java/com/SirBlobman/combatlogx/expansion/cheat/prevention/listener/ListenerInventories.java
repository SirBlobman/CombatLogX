package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class ListenerInventories implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    public ListenerInventories(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onOpenInventory(InventoryOpenEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("inventories.close-on-tag")) return;

        HumanEntity human = e.getPlayer();
        if(!(human instanceof Player)) return;

        Player player = (Player) human;
        ICombatManager manager = this.plugin.getCombatManager();
        if(!manager.isInCombat(player)) return;

        e.setCancelled(true);
        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.inventory.no-opening");
        this.plugin.sendMessage(player, message);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        InventoryView openView = player.getOpenInventory();
        if(openView == null) return;

        Inventory topInv = openView.getTopInventory();
        if(topInv == null) return;

        InventoryType type = openView.getType();
        if(type == InventoryType.CRAFTING) return;

        player.closeInventory();
        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.inventory.force-closed");
        this.plugin.sendMessage(player, message);
    }
}
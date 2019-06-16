package com.SirBlobman.expansion.compatcitizens.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.SirBlobman.api.utility.ItemUtil;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;
import com.SirBlobman.expansion.compatcitizens.config.ConfigData;
import com.SirBlobman.expansion.compatcitizens.trait.TraitCombatLogX;

import java.util.List;
import java.util.UUID;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class ListenPlayerJoin implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeLogin(AsyncPlayerPreLoginEvent e) {
        if(!ConfigCitizens.getOption("citizens.npc.prevent login", false)) return;
        
        UUID uuid = e.getUniqueId();
        NPC npc = getNPC(uuid);
        if(npc == null) return;
        
        String message = ConfigLang.get("messages.expansions.citizens compatibility.kick message");
        e.setKickMessage(message);
        e.setLoginResult(Result.KICK_OTHER);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        
        NPC npc = getNPC(player);
        if(npc != null) npc.despawn(DespawnReason.PLUGIN);
        
        SchedulerUtil.runLater(5L, () -> punish(player));
    }
    
    public void punish(Player player) {
        Util.debug("[Citizens Compatibility] '" + player.getName() + "' joined, checking if they need to be punished...");
        boolean punish = ConfigData.get(player, "punish", false);
        if(!punish) {
            Util.debug("[Citizens Compatibility] Punishment is false in user data file, ignoring.");
            return;
        }
        Util.debug("[Citizens Compatibility] Punishment is true in user data file, punishing...");
        
        Location lastLocation = ConfigData.get(player, "last location", player.getLocation());
        Util.debug("[Citizens Compatibility] Teleported player to '" + lastLocation + "'.");
        player.teleport(lastLocation);
        
        if(ConfigCitizens.getOption("citizens.npc.retag player on login", true)) {
            CombatUtil.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
            Util.debug("[Citizens Compatibility] Tagged player");
        }
        
        double lastHealth = ConfigData.get(player, "last health", player.getHealth());
        
        if(ConfigCitizens.getOption("citizens.npc.store inventory", true)) {
            if(lastHealth > 0.0D) transferInventoryToPlayer(player);
            else {
                PlayerInventory playerInv = player.getInventory();
                playerInv.setArmorContents(new ItemStack[] {ItemUtil.AIR, ItemUtil.AIR, ItemUtil.AIR, ItemUtil.AIR});
                playerInv.clear();
                player.updateInventory();
            }
            
            Util.debug("[Citizens Compatibility] Updated player inventory");
        }
        
        player.setHealth(lastHealth);
        Util.debug("[Citizens Compatibility] Set player health to '" + lastHealth + "'.");
        
        ConfigData.force(player, "punish", false);
        Util.debug("[Citizens Compatibility] Player punishment ended, user data punishment set to false.");
    }
    
    public NPC getNPC(OfflinePlayer player) {
        if(player == null) return null;
        
        UUID uuid = player.getUniqueId();
        return getNPC(uuid);
    }
    
    public NPC getNPC(UUID playerUUID) {
        if(playerUUID == null) return null;
        
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        for(NPC npc : npcRegistry) {
            if(!npc.hasTrait(TraitCombatLogX.class)) continue;
            
            TraitCombatLogX traitCLX = npc.getTrait(TraitCombatLogX.class);
            OfflinePlayer npcOwner = traitCLX.getOwner();
            if(npcOwner == null) continue;
            
            UUID uuid = npcOwner.getUniqueId();
            if(uuid.equals(playerUUID)) return npc;
        }
        
        return null;
    }
    
    public void transferInventoryToPlayer(Player player) {
        if(player == null) return;
        
        PlayerInventory playerInv = player.getInventory();
        final ItemStack air = new ItemStack(Material.AIR);
        
        List<ItemStack> itemList = ConfigData.get(player, "inventory data.items", Util.newList());
        ItemStack[] contents = itemList.toArray(new ItemStack[0]);
        playerInv.setContents(contents);

        ItemStack itemHelmet = ConfigData.get(player, "inventory data.helmet", air);
        ItemStack itemChestplate = ConfigData.get(player, "inventory data.chestplate", air);
        ItemStack itemLeggings = ConfigData.get(player, "inventory data.leggings", air);
        ItemStack itemBoots = ConfigData.get(player, "inventory data.boots", air);
        playerInv.setHelmet(itemHelmet);
        playerInv.setChestplate(itemChestplate);
        playerInv.setLeggings(itemLeggings);
        playerInv.setBoots(itemBoots);
        
        player.updateInventory();
        ConfigData.force(player, "inventory data", null);
    }
}
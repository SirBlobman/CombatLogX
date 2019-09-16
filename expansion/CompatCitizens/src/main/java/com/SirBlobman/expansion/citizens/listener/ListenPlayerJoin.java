package com.SirBlobman.expansion.citizens.listener;

import java.util.List;
import java.util.UUID;

import com.SirBlobman.api.utility.ItemUtil;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.citizens.config.ConfigCitizens;
import com.SirBlobman.expansion.citizens.config.ConfigData;
import com.SirBlobman.expansion.citizens.trait.TraitCombatLogX;

import org.bukkit.Location;
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

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class ListenPlayerJoin implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void beforeLogin(AsyncPlayerPreLoginEvent e) {
        if(!ConfigCitizens.getOption("citizens.npc.prevent login", false)) return;
        
        UUID uuid = e.getUniqueId();
        NPC npc = getNPC(uuid);
        if(npc == null) return;
        
        String message = ConfigLang.get("messages.expansions.citizens compatibility.kick message");
        e.setKickMessage(message);
        e.setLoginResult(Result.KICK_OTHER);
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setCanPickupItems(false);
        Util.debug("[Citizens Compatibility] Checking '" + player.getName() + "' for NPCs");
        
        NPC npc = getNPC(player);
        if(npc != null) {
            Util.debug("Found NPC, despawning.");
            npc.despawn(DespawnReason.PLUGIN);
        }
        
        SchedulerUtil.runLater(1L, () -> {
            punish(player);
            player.setCanPickupItems(true);
        });
    }

    private NPC getNPC(OfflinePlayer player) {
        if(player == null) return null;

        UUID uuid = player.getUniqueId();
        return getNPC(uuid);
    }

    private NPC getNPC(UUID playerUUID) {
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
    
    private void punish(Player player) {
        Util.debug("[Citizens Compatibility] '" + player.getName() + "' joined, checking if they need to be punished...");
        boolean punish = ConfigData.get(player, "punish", false);
        if(!punish) {
            Util.debug("[Citizens Compatibility] Punishment is false in user data file, ignoring.");
            return;
        }
        Util.debug("[Citizens Compatibility] Punishment is true in user data file, punishing...");

        fixLocation(player);
        fixTagStatus(player);
        fixInventory(player);
        fixHealth(player);
        
        ConfigData.force(player, "punish", false);
        Util.debug("[Citizens Compatibility] Player punishment ended, user data punishment set to false.");
    }

    private void fixLocation(Player player) {
        Location lastLocation = ConfigData.get(player, "last location", player.getLocation());
        player.teleport(lastLocation);

        Util.debug("[Citizens Compatibility] Teleport player to '" + lastLocation + "'.");
        ConfigData.force(player, "last location", null);
    }

    private void fixTagStatus(Player player) {
        boolean shouldTag = ConfigCitizens.getOption("citizens.npc.retag player on login", true);
        if(!shouldTag) return;

        boolean tagged = CombatUtil.tag(player, null, PlayerTagEvent.TagType.UNKNOWN, PlayerTagEvent.TagReason.UNKNOWN);
        Util.debug("[Citizens Compatibility] Result of attempting to re-tag player: " + (tagged ? "Success" : "Failure"));
    }

    private void fixInventory(Player player) {
        boolean storeInventory = ConfigCitizens.getOption("citizens.npc.store inventory", true);
        if(!storeInventory) return;

        double lastHealth = ConfigData.get(player, "last health", player.getHealth());
        if(lastHealth > 0.0D) {
            transferInventoryToPlayer(player);
            ConfigData.force(player, "inventory data.items", null);
            ConfigData.force(player, "inventory data.armor", null);
            return;
        }

        PlayerInventory playerInv = player.getInventory();
        playerInv.setArmorContents(new ItemStack[] {ItemUtil.getAir(), ItemUtil.getAir(), ItemUtil.getAir(), ItemUtil.getAir()});
        playerInv.clear();
        player.updateInventory();

        ConfigData.force(player, "inventory data.items", null);
        ConfigData.force(player, "inventory data.armor", null);
    }

    private void fixHealth(Player player) {
        double lastHealth = ConfigData.get(player, "last health", player.getHealth());
        player.setHealth(lastHealth);

        Util.debug("[Citizens Compatibility] Set player health to '" + lastHealth + "'.");
    }
    
    private void transferInventoryToPlayer(Player player) {
        if(player == null) return;
        
        PlayerInventory playerInv = player.getInventory();
        
        List<ItemStack> itemList = ConfigData.get(player, "inventory data.items", Util.newList());
        ItemStack[] contents = itemList.toArray(new ItemStack[0]);
        playerInv.setContents(contents);

        List<ItemStack> armorList = ConfigData.get(player, "inventory data.armor", Util.newList());
        ItemStack[] armor = armorList.toArray(new ItemStack[0]);
        playerInv.setArmorContents(armor);
        
        player.updateInventory();
    }
}
package com.SirBlobman.expansion.compatcitizens.listener;

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
        if(npc == null) return;
        
        npc.despawn(DespawnReason.PLUGIN);
        SchedulerUtil.runLater(5L, () -> punish(player));
    }
    
    public void punish(Player player) {
        boolean punish = ConfigData.get(player, "punish", false);
        if(!punish) return;
        
        double lastHealth = ConfigData.get(player, "last health", player.getHealth());
        player.setHealth(lastHealth);
        
        if(player.getHealth() > 0.0D) {
            Location lastLocation = ConfigData.get(player, "last location", player.getLocation());
            player.teleport(lastLocation);
            
            if(ConfigCitizens.getOption("citizens.npc.store inventory", true)) {
                List<ItemStack> lastInventory = ConfigData.get(player, "last inventory", Util.newList());
                ItemStack[] lastContents = lastInventory.toArray(new ItemStack[0]);
                player.getInventory().setContents(lastContents);
                player.updateInventory();
            }
            
            if(ConfigCitizens.getOption("citizens.npc.retag player", true)) {
                CombatUtil.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
            }
        }
        
        ConfigData.force(player, "punish", false);
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
}
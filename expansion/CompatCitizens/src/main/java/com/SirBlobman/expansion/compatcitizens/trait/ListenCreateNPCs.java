package com.SirBlobman.expansion.compatcitizens.trait;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent.PunishReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.compatcitizens.CompatCitizens;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;

import java.util.UUID;

import org.mcmonkey.sentinel.SentinelTrait;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Inventory;

public class ListenCreateNPCs implements Listener {
    private final CompatCitizens expansion;
    public ListenCreateNPCs(CompatCitizens expansion) {
        this.expansion = expansion;
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPunish(PlayerPunishEvent e) {
        PunishReason reason = e.getReason();
        if(reason == PunishReason.UNKNOWN) return;
        
        if(ConfigCitizens.getOption("citizens.cancel other punishments", true)) e.setCancelled(true);
        
        Player player = e.getPlayer();
        createNPC(player);
    }
    
    public void createNPC(Player player) {
        Location location = player.getLocation();
        if(location == null) {
            this.expansion.print("Failed to get location for player '" + player.getName() + ", forcing regular punishment instead of NPC...");
            CombatUtil.forcePunish(player);
            return;
        }
        location = location.clone();
        
        EntityType type = getTypeForNPC();
        if(type == null) {
            type = EntityType.PLAYER;
            this.expansion.print("Invalid EntityType in config, defaulting to EntityType.PLAYER");
        }
        
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        NPC npc = npcRegistry.createNPC(type, player.getName());
        npc.setProtected(false);
        
        boolean mobTargetable = ConfigCitizens.getOption("citizens.npc.mob targeting", true);
        boolean storeInventory = ConfigCitizens.getOption("citizens.npc.store inventory", true);
        
        if(mobTargetable) {
            MetadataStore data = npc.data();
            data.set(NPC.TARGETABLE_METADATA, true);
        }
        
        if(storeInventory) {
            Inventory invTrait = npc.getTrait(Inventory.class);
            ItemStack[] contents = player.getInventory().getContents();
            invTrait.setContents(contents);
        }
        
        TraitCombatLogX traitCLX = npc.getTrait(TraitCombatLogX.class);
        traitCLX.setOwner(player);
        
        boolean sentinel = PluginUtil.isEnabled("Sentinel", "mcmonkey") && ConfigCitizens.getOption("citizens.sentinel.use sentinel", true);
        boolean attackFirst = ConfigCitizens.getOption("citizens.sentinel.attack first", false);
        if(sentinel) {
            SentinelTrait sentinelTrait = npc.getTrait(SentinelTrait.class);
            sentinelTrait.setInvincible(false);
            
            LivingEntity enemy = CombatUtil.getEnemy(player);
            if(enemy != null) {
                UUID enemyID = enemy.getUniqueId();
                sentinelTrait.addTarget(enemyID);
                if(attackFirst) sentinelTrait.chase(enemy);
            }
            
            double health = player.getHealth();
            sentinelTrait.setHealth(health);
        }
        
        npc.spawn(location, SpawnReason.CREATE);
        
        if(type.isAlive()) {
            LivingEntity npcLiving = (LivingEntity) npc.getEntity();
            
            double health = player.getHealth();
            npcLiving.setHealth(health);
            
            NMS_Handler nms = NMS_Handler.getHandler();
            double maxHealth = nms.getMaxHealth(player);
            nms.setMaxHealth(player, maxHealth);
        }
    }
    
    private EntityType getTypeForNPC() {
        String configType = ConfigCitizens.getOption("citizens.npc.entity type", EntityType.PLAYER.name());
        try {
            EntityType type = EntityType.valueOf(configType);
            return type;
        } catch(IllegalArgumentException error) {
            return null;
        }
    }
}
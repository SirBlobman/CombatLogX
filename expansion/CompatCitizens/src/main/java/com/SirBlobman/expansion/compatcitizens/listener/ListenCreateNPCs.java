package com.SirBlobman.expansion.compatcitizens.listener;

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
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.compatcitizens.CompatCitizens;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;
import com.SirBlobman.expansion.compatcitizens.trait.TraitCombatLogX;

import java.util.UUID;

import org.mcmonkey.sentinel.SentinelTrait;
import org.mcmonkey.sentinel.targeting.SentinelTargetLabel;

import net.citizensnpcs.api.CitizensAPI;
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
        LivingEntity enemy = CombatUtil.getEnemy(player);
        
        EntityType type = getTypeForNPC();
        if(type == null) {
            type = EntityType.PLAYER;
            this.expansion.print("Invalid EntityType in config, defaulting to EntityType.PLAYER");
        }
        
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        NPC npc = npcRegistry.createNPC(type, player.getName());
        npc.setProtected(false);
        Util.debug("[Citizens Compatibility] Created NPC and set protected to 'false'.");
        
        Util.debug("[Citizens Compatibility] Attempting to spawn NPC for player '" + player.getName() + "'...");
        if(location == null) {
            this.expansion.print("Failed to get location for player '" + player.getName() + ", forcing regular punishment instead of NPC...");
            CombatUtil.forcePunish(player);
            return;
        }
        
        location = location.clone();
        npc.spawn(location);

        Util.debug("[Citizens Compatibility] Spawned NPC, checking if sentinel settings are enabled...");
        boolean sentinel = PluginUtil.isEnabled("Sentinel", "mcmonkey") && ConfigCitizens.getOption("citizens.sentinel.use sentinel", true);
        if(sentinel) {
            Util.debug("[Citizens Compatibility] Sentinel enabled");
            SentinelTrait sentinelTrait = npc.getTrait(SentinelTrait.class);
            sentinelTrait.setInvincible(false);
            Util.debug("[Citizens Compatibility] Added trait to NPC and set it to not be invincible.");
            
            sentinelTrait.realistic = true;
            sentinelTrait.fightback = true;
            Util.debug("[Citizens Compatibility] Enabled realistic and fightback");

            sentinelTrait.respawnTime = -1L;
            Util.debug("[Citizens Compatibility] Set respawn mode to 'delete after death'");
            
            if(enemy != null) {
                UUID enemyID = enemy.getUniqueId();
                
                SentinelTargetLabel enemyLabel = new SentinelTargetLabel("uuid:" + enemyID);
                enemyLabel.addToList(sentinelTrait.allTargets);
                Util.debug("[Citizens Compatibility] Added player enemy as sentinel target");
            }
        }
        
        SchedulerUtil.runLater(25L, () -> {
            Util.debug("[Citizens Compatibility] Checking if NPC is still spawned after 5 ticks...");
            if(!npc.isSpawned()) {
                Util.debug("[Citizens Compatibility] Failed to spawn npc for player '" + player.getName() + "', forcing regular punishment.");
                CombatUtil.forcePunish(player);
                return;
            }
            Util.debug("[Citizens Compatibility] NPC is spawned, running other tasks...");
            
            if(npc.getEntity().getType().isAlive()) {
                LivingEntity npcLiving = (LivingEntity) npc.getEntity();
                
                double health = player.getHealth();
                npcLiving.setHealth(health);
                
                NMS_Handler nms = NMS_Handler.getHandler();
                double maxHealth = nms.getMaxHealth(player);
                nms.setMaxHealth(player, maxHealth);
                Util.debug("[Citizens Compatibility] Set npc health to match player health");
            }
            
            boolean mobTargetable = ConfigCitizens.getOption("citizens.npc.mob targeting", true);
            boolean storeInventory = ConfigCitizens.getOption("citizens.npc.store inventory", true);
            
            TraitCombatLogX traitCLX = npc.getTrait(TraitCombatLogX.class);
            traitCLX.setOwner(player);
            if(enemy instanceof Player) traitCLX.setEnemy((Player) enemy);
            Util.debug("[Citizens Compatibility] Added CombatLogX trait and set owner/enemy for NPC");
            
            if(storeInventory) {
                Inventory invTrait = npc.getTrait(Inventory.class);
                ItemStack[] contents = player.getInventory().getContents();
                invTrait.setContents(contents);
                Util.debug("[Citizens Compatibility] Stored inventory of '" + player.getName() + "' in NPC");
            }
            
            if(mobTargetable) {
                npc.data().set(NPC.TARGETABLE_METADATA, true);
                Util.debug("[Citizens Compatibility] Set npc to be targetable by mobs");
            }
            
            boolean sentinel2 = PluginUtil.isEnabled("Sentinel", "mcmonkey") && ConfigCitizens.getOption("citizens.sentinel.use sentinel", true);
            if(sentinel2) {
                SentinelTrait sentinelTrait = npc.getTrait(SentinelTrait.class);
                boolean attackFirst = ConfigCitizens.getOption("citizens.sentinel.attack first", false);
                if(attackFirst) {
                    sentinelTrait.attackHelper.chase(enemy);
                    Util.debug("[Citizens Compatibility] Enabled NPC to attack first.");
                }
                
                double health = player.getHealth();
                sentinelTrait.setHealth(health);
                Util.debug("[Citizens Compatibility] Set Sentinel health again just in case");
            }
            
            Util.debug("[Citizens Compatibility] NPC successfully created!");
        });
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
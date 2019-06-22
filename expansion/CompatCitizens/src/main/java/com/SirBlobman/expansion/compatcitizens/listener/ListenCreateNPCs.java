package com.SirBlobman.expansion.compatcitizens.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent.PunishReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.compatcitizens.CompatCitizens;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;
import com.SirBlobman.expansion.compatcitizens.trait.TraitCombatLogX;

import java.util.Arrays;

import org.mcmonkey.sentinel.SentinelTrait;
import org.mcmonkey.sentinel.targeting.SentinelTargetLabel;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.api.trait.trait.Owner;

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
        LivingEntity enemy = e.getPreviousEnemy();
        createNPC(player, enemy);
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
    
    public void createNPC(Player player, LivingEntity enemy) {
        Location location = player.getLocation();
        
        EntityType type = getTypeForNPC();
        if(type == null) {
            type = EntityType.PLAYER;
            this.expansion.print("Invalid EntityType in config, defaulting to EntityType.PLAYER");
        }
        
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        NPC npc = npcRegistry.createNPC(type, player.getName());
        if(location == null) {
            this.expansion.print("Failed to get location for player '" + player.getName() + ", forcing regular punishment instead of NPC...");
            CombatUtil.forcePunish(player);
            return;
        }
        
        location = location.clone();
        boolean isSpawned = npc.spawn(location);
        
        if(!isSpawned) {
            CombatUtil.forcePunish(player);
            return;
        }
        npc.setProtected(false);
        
        setOptions(npc, player, enemy);
        setSentinel(npc, player, enemy);
    }
    
    private void setOptions(NPC npc, Player player, LivingEntity enemy) {
        boolean mobTargetable = ConfigCitizens.getOption("citizens.npc.mob targeting", true);
        npc.data().set(NPC.TARGETABLE_METADATA, !mobTargetable);
        
        npc.removeTrait(Owner.class);
        
        TraitCombatLogX traitCLX = npc.getTrait(TraitCombatLogX.class);
        traitCLX.setOwner(player);
        if(enemy instanceof Player) traitCLX.setEnemy((Player) enemy);
        
        boolean storeInventory = ConfigCitizens.getOption("citizens.npc.store inventory", true);
        if(storeInventory) transferInventoryToNPC(player, npc);
        
        if(npc.getEntity().getType().isAlive()) {
            LivingEntity npcLiving = (LivingEntity) npc.getEntity();
            
            double health = player.getHealth();
            npcLiving.setHealth(health);
            
            NMS_Handler nms = NMS_Handler.getHandler();
            double maxHealth = nms.getMaxHealth(player);
            nms.setMaxHealth(player, maxHealth);
        }
    }
    
    private void setSentinel(NPC npc, Player player, LivingEntity enemy) {
        Util.debug("[Citizens Compatibility] Spawned NPC, checking if sentinel settings are enabled...");
        boolean sentinel = PluginUtil.isEnabled("Sentinel", "mcmonkey") && ConfigCitizens.getOption("citizens.sentinel.use sentinel", true);
        if(sentinel) {
            Util.debug("[Citizens Compatibility] Sentinel enabled");
            SentinelTrait sentinelTrait = npc.getTrait(SentinelTrait.class);
            sentinelTrait.setInvincible(false);
            Util.debug("[Citizens Compatibility] Added trait to NPC and set it to not be invincible.");
            
            sentinelTrait.respawnTime = -1L;
            Util.debug("[Citizens Compatibility] Set respawn mode to 'delete after death'");
            
            if(enemy != null) {
                boolean attackFirst = ConfigCitizens.getOption("citizens.sentinel.attack first", false);
                if(attackFirst) {
                    SentinelTargetLabel targetLabel = new SentinelTargetLabel("uuid:" + enemy.getUniqueId().toString());
                    targetLabel.addToList(sentinelTrait.allTargets);
                    Util.debug("[Citizens Compatibility] Added player enemy as 'attack first' sentinel target");
                }
            }
            
            double health = player.getHealth();
            sentinelTrait.setHealth(health);
            Util.debug("[Citizens Compatibility] Set Sentinel health again just in case");
        }
    }
    
    private void transferInventoryToNPC(Player player, NPC npc) {
        if(player == null || npc == null) return;
        
        final ItemStack air = new ItemStack(Material.AIR);
        ItemStack[] airArmor = new ItemStack[4]; Arrays.fill(airArmor, air);
        ItemStack[] airInventory = new ItemStack[4 * 9]; Arrays.fill(airInventory, air);
        
        PlayerInventory playerInv = player.getInventory();
        ItemStack itemHelmet = playerInv.getHelmet();
        ItemStack itemChestplate = playerInv.getChestplate();
        ItemStack itemLeggings = playerInv.getLeggings();
        ItemStack itemBoots = playerInv.getBoots();
        playerInv.setArmorContents(airArmor);
        
        ItemStack[] contents = playerInv.getContents();
        playerInv.setContents(airInventory);
        
        Equipment npcEquip = npc.getTrait(Equipment.class);
        npcEquip.set(EquipmentSlot.HELMET, itemHelmet);
        npcEquip.set(EquipmentSlot.CHESTPLATE, itemChestplate);
        npcEquip.set(EquipmentSlot.LEGGINGS, itemLeggings);
        npcEquip.set(EquipmentSlot.BOOTS, itemBoots);
        
        Inventory npcInv = npc.getTrait(Inventory.class);
        npcInv.setContents(contents);
        
        player.updateInventory();
    }
}
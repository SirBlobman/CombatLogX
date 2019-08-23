package com.SirBlobman.expansion.citizens.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.api.utility.ItemUtil;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent.PunishReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.citizens.CompatCitizens;
import com.SirBlobman.expansion.citizens.config.ConfigCitizens;
import com.SirBlobman.expansion.citizens.config.ConfigData;
import com.SirBlobman.expansion.citizens.trait.TraitCombatLogX;

import java.util.Arrays;
import java.util.Map;

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
    
    private void debug(String message) {
        message = "[Debug] " + message;
        this.expansion.print(message);
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
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
        
        npc.removeTrait(Owner.class);
        
        TraitCombatLogX traitCLX = npc.getTrait(TraitCombatLogX.class);
        traitCLX.setOwner(player);
        if(enemy instanceof Player) traitCLX.setEnemy((Player) enemy);
        
        location = location.clone();
        boolean isSpawned = npc.spawn(location);
        
        if(!isSpawned) {
            CombatUtil.forcePunish(player);
            return;
        }
        
        setOptions(npc, player, enemy);
        setSentinel(npc, player, enemy);
    }
    
    private void setOptions(NPC npc, Player player, LivingEntity enemy) {
        npc.setProtected(false);
        
        boolean storeInventory = ConfigCitizens.getOption("citizens.npc.store inventory", true);
        debug("NPC Store Inventory Value: " + storeInventory);
        if(storeInventory) transferInventoryToNPC(player, npc);
        
        boolean mobTargetable = ConfigCitizens.getOption("citizens.npc.mob targeting", true);
        if(mobTargetable) {
            npc.data().set(NPC.TARGETABLE_METADATA, false);
            Entity npcEntity = npc.getEntity();
            if(npcEntity instanceof LivingEntity) {
                LivingEntity npcLiving = (LivingEntity) npcEntity;
                
                for(Entity entity : npcLiving.getNearbyEntities(16.0, 16.0, 16.0)) {
                    if(!(entity instanceof Monster)) return;
                    
                    Monster monster = (Monster) entity;
                    monster.setTarget(npcLiving);
                }
            }
        };
        
        if(npc.getEntity().getType().isAlive()) {
            LivingEntity npcLiving = (LivingEntity) npc.getEntity();
            
            NMS_Handler nms = NMS_Handler.getHandler();
            double maxHealth = Math.max(player.getHealth(), nms.getMaxHealth(player));
            nms.setMaxHealth(npcLiving, maxHealth);
            
            double health = player.getHealth();
            npcLiving.setHealth(health);
        }
    }
    
    private void setSentinel(NPC npc, Player player, LivingEntity enemy) {
        boolean sentinel = PluginUtil.isEnabled("Sentinel", "mcmonkey") && ConfigCitizens.getOption("citizens.sentinel.use sentinel", true);
        if(sentinel) {
            SentinelTrait sentinelTrait = npc.getTrait(SentinelTrait.class);
            sentinelTrait.setInvincible(false);
            
            sentinelTrait.respawnTime = -1L;
            
            if(enemy != null) {
                boolean attackFirst = ConfigCitizens.getOption("citizens.sentinel.attack first", false);
                if(attackFirst) {
                    SentinelTargetLabel targetLabel = new SentinelTargetLabel("uuid:" + enemy.getUniqueId().toString());
                    targetLabel.addToList(sentinelTrait.allTargets);
                }
            }
            
            double health = player.getHealth();
            sentinelTrait.setHealth(health);
        }
    }
    
    private void transferInventoryToNPC(Player player, NPC npc) {
        if(player == null || npc == null) return;
        
        PlayerInventory playerInv = player.getInventory();
        
        Equipment equipment = npc.getTrait(Equipment.class);
        ItemStack helmet = copyItem(playerInv.getHelmet());
        ItemStack chestplate = copyItem(playerInv.getChestplate());
        ItemStack leggings = copyItem(playerInv.getLeggings());
        ItemStack boots = copyItem(playerInv.getBoots());
        if(NMS_Handler.getMinorVersion() > 8) {
            ItemStack offHand = copyItem(playerInv.getItemInOffHand());
            equipment.set(EquipmentSlot.OFF_HAND, offHand);
        }
        equipment.set(EquipmentSlot.HELMET, helmet);
        equipment.set(EquipmentSlot.CHESTPLATE, chestplate);
        equipment.set(EquipmentSlot.LEGGINGS, leggings);
        equipment.set(EquipmentSlot.BOOTS, boots);
        
        Inventory inventory = npc.getTrait(Inventory.class);
        ItemStack[] contents = copyItems(playerInv, 0, 4 * 9);
        inventory.setContents(contents);
        
        playerInv.clear();
        player.updateInventory();
        
        Map<String, Object> inventoryData = ListenHandleNPCs.getInventoryData(npc);
        ConfigData.force(player, "inventory data", inventoryData);
    }
    
    private ItemStack copyItem(ItemStack item) {
        if(ItemUtil.isAir(item)) return new ItemStack(Material.AIR);
        
        return item.clone();
    }
    
    private ItemStack[] copyItems(org.bukkit.inventory.Inventory inventory, int minSlot, int maxSlot) {
        ItemStack[] itemArray = new ItemStack[maxSlot];
        Arrays.fill(itemArray, new ItemStack(Material.AIR));
        
        int inventorySize = inventory.getSize();
        for(int slot = minSlot; slot < inventorySize && slot < maxSlot; slot++) {
            ItemStack item = inventory.getItem(slot);
            itemArray[slot] = copyItem(item);
        }
        
        return itemArray;
    }
}
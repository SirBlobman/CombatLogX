package com.SirBlobman.expansion.citizens.listener;

import java.util.List;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.api.utility.ItemUtil;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent.PunishReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.citizens.CompatCitizens;
import com.SirBlobman.expansion.citizens.config.ConfigCitizens;
import com.SirBlobman.expansion.citizens.config.ConfigData;
import com.SirBlobman.expansion.citizens.trait.TraitCombatLogX;
import com.SirBlobman.expansion.citizens.utility.SentinelUtil;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Owner;

public class ListenCreateNPCs implements Listener {
    private final CompatCitizens expansion;
    public ListenCreateNPCs(CompatCitizens expansion) {
        this.expansion = expansion;
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
            return EntityType.valueOf(configType);
        } catch(IllegalArgumentException error) {
            return null;
        }
    }
    
    private void createNPC(Player player, LivingEntity enemy) {
        Location location = player.getLocation();
        
        EntityType type = getTypeForNPC();
        if(type == null) {
            type = EntityType.PLAYER;
            this.expansion.print("Invalid EntityType in config, defaulting to EntityType.PLAYER");
        }
        
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        NPC npc = npcRegistry.createNPC(type, player.getName());
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
        
        setOptions(npc, player);
        SentinelUtil.setSentinel(npc, player, enemy);
    }
    
    private void setOptions(NPC npc, Player player) {
        npc.setProtected(false);
        
        boolean storeInventory = ConfigCitizens.getOption("citizens.npc.store inventory", true);
        if(storeInventory) transferInventoryToNPC(player, npc);
        
        setAliveOptions(npc, player);
    }

    private void setAliveOptions(NPC npc, Player player) {
        if(npc == null || player == null) return;

        Entity entity = npc.getEntity();
        EntityType npcType = entity.getType();
        if(!npcType.isAlive()) return;

        LivingEntity npcLiving = (LivingEntity) entity;
        NMS_Handler nms = NMS_Handler.getHandler();

        double maxHealth = Math.max(player.getHealth(), nms.getMaxHealth(player));
        nms.setMaxHealth(npcLiving, maxHealth);

        double health = player.getHealth();
        npcLiving.setHealth(health);

        boolean mobTargetable = ConfigCitizens.getOption("citizens.npc.mob targeting", true);
        if(mobTargetable) setMobTargetable(npc);
    }

    private void setMobTargetable(NPC npc) {
        if(npc == null) return;

        Entity entity = npc.getEntity();
        EntityType npcType = entity.getType();
        if(!npcType.isAlive()) return;

        LivingEntity npcLiving = (LivingEntity) entity;
        List<Entity> nearbyList = npcLiving.getNearbyEntities(16.0D, 16.0D, 16.0D);
        for(Entity nearby : nearbyList) {
            if(!(nearby instanceof Monster)) continue;

            Monster monster = (Monster) nearby;
            monster.setTarget(npcLiving);
        }
    }
    
    private void transferInventoryToNPC(Player player, NPC npc) {
        if(player == null || npc == null) return;
        
        PlayerInventory playerInv = player.getInventory();

        Entity entity = npc.getEntity();
        if(entity instanceof Player || entity instanceof Zombie || entity instanceof Skeleton) {
            Equipment equipment = npc.getTrait(Equipment.class);
            equipment.set(Equipment.EquipmentSlot.HELMET, copyItem(playerInv.getHelmet()));
            equipment.set(Equipment.EquipmentSlot.CHESTPLATE, copyItem(playerInv.getChestplate()));
            equipment.set(Equipment.EquipmentSlot.LEGGINGS, copyItem(playerInv.getLeggings()));
            equipment.set(Equipment.EquipmentSlot.BOOTS, copyItem(playerInv.getBoots()));

            @SuppressWarnings("deprecation")
            ItemStack handItem = copyItem(NMS_Handler.getMinorVersion() > 8 ? playerInv.getItemInMainHand() : playerInv.getItemInHand());
            equipment.set(Equipment.EquipmentSlot.HAND, handItem);

            if(NMS_Handler.getMinorVersion() > 8) {
                ItemStack offItem = copyItem(playerInv.getItemInOffHand());
                equipment.set(Equipment.EquipmentSlot.OFF_HAND, offItem);
            }
        }

        ItemStack[] contents = playerInv.getContents().clone();
        List<ItemStack> contentsList = Util.newList(contents);

        ItemStack[] armor = playerInv.getArmorContents().clone();
        List<ItemStack> armorList = Util.newList(armor);

        ConfigData.force(player, "inventory data.items", contentsList);
        ConfigData.force(player, "inventory data.armor", armorList);

        playerInv.clear();
        player.updateInventory();
    }

    private ItemStack copyItem(ItemStack item) {
        if(ItemUtil.isAir(item)) return ItemUtil.getAir();

        return item.clone();
    }
}
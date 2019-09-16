package com.SirBlobman.expansion.citizens.listener;

import com.SirBlobman.api.utility.ItemUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.citizens.config.ConfigCitizens;
import com.SirBlobman.expansion.citizens.config.ConfigData;
import com.SirBlobman.expansion.citizens.trait.TraitCombatLogX;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ListenHandleNPCs implements Listener {
    public static boolean isInvalid(NPC npc) {
        if(npc == null) return true;
        return !npc.hasTrait(TraitCombatLogX.class);
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void beforeDamage(NPCDamageByEntityEvent e) {
        NPC npc = e.getNPC();
        if(isInvalid(npc)) return;
        
        TraitCombatLogX trait = npc.getTrait(TraitCombatLogX.class);
        if(ConfigCitizens.getOption("citizens.npc.reset timer on damage", false)) trait.extendTimeUntilRemove();
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onDeath(NPCDeathEvent e) {
        NPC npc = e.getNPC();
        if(isInvalid(npc)) return;
        
        e.getDrops().clear();
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void beforeDespawn(NPCDespawnEvent e) {
        DespawnReason reason = e.getReason();
        if(reason == DespawnReason.PENDING_RESPAWN) return;
        
        NPC npc = e.getNPC();
        if(isInvalid(npc)) return;

        TraitCombatLogX trait = npc.getTrait(TraitCombatLogX.class);
        OfflinePlayer owner = trait.getOwner();
        if(owner == null) return;
        
        if(reason == DespawnReason.DEATH) checkInventory(owner, npc);

        Entity npcEntity = npc.getEntity();
        checkHealth(owner, npcEntity);
        checkLocation(owner, npcEntity);

        ConfigData.force(owner, "punish", true);
        SchedulerUtil.runLater(1L, npc::destroy);
    }

    private void checkHealth(OfflinePlayer owner, Entity npcEntity) {
        double health = 0.0D;
        if(npcEntity instanceof LivingEntity) {
            LivingEntity npcLiving = (LivingEntity) npcEntity;
            health = npcLiving.getHealth();
        }
        ConfigData.force(owner, "last health", health);
    }

    private void checkLocation(OfflinePlayer owner, Entity npcEntity) {
        Location location = npcEntity.getLocation();
        ConfigData.force(owner, "last location", location);
    }

    private void checkInventory(OfflinePlayer owner, NPC npc) {
        boolean storeInventory = ConfigCitizens.getOption("citizens.npc.store inventory", true);
        if(!storeInventory) return;

        if(npc.hasTrait(Equipment.class)) {
            Equipment equipment = npc.getTrait(Equipment.class);
            npc.removeTrait(Equipment.class);
        }

        List<ItemStack> contentsList = ConfigData.get(owner, "inventory data.items", Util.newList());
        List<ItemStack> armorList = ConfigData.get(owner, "inventory data.armor", Util.newList());
        if(contentsList.isEmpty() && armorList.isEmpty()) return;

        Entity npcEntity = npc.getEntity();
        Location location = npcEntity.getLocation();
        World world = location.getWorld();
        if(world == null) return;

        final int contentsListSize = contentsList.size();
        for(int i = 0; i < contentsListSize; i++) {
            ItemStack drop = contentsList.get(i);
            if(!ItemUtil.isAir(drop)) world.dropItemNaturally(location, drop);
            contentsList.set(i, null);
        }

        final int armorListSize = armorList.size();
        for(int i = 0; i < armorListSize; i++) {
            ItemStack drop = armorList.get(i);
            if(!ItemUtil.isAir(drop)) world.dropItemNaturally(location, drop);
            armorList.set(i, null);
        }

        ConfigData.force(owner, "inventory data.items", contentsList);
    }
}

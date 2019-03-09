package com.SirBlobman.expansion.compatcitizens.listener;

import com.SirBlobman.combatlogx.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent.PunishReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.legacy.LegacyHandler;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;
import com.SirBlobman.expansion.compatcitizens.config.ConfigData;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang.Validate;
import org.mcmonkey.sentinel.SentinelTrait;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Inventory;

public class NPCManager implements Listener {
    private static void createNPC(Player player) {
        String entityType = ConfigCitizens.ENTITY_TYPE;
        EntityType type;
        try {
            type = EntityType.valueOf(entityType);
        } catch (Throwable ex) {
            String error = "An error has occured trying to create an NPC: '" + entityType + "' is not valid.\nDefaulting to PLAYER";
            Util.print(error);
            ex.printStackTrace();
            ConfigCitizens.ENTITY_TYPE = EntityType.PLAYER.name();
            type = EntityType.PLAYER;
        }
        
        boolean cloneInventory = ConfigCitizens.STORE_INVENTORY && (type == EntityType.PLAYER);
        boolean sentinel = ConfigCitizens.USE_SENTINELS && PluginUtil.isEnabled("Sentinel", "mcmonkey");
        
        NPCRegistry reg = CitizensAPI.getNPCRegistry();
        NPC npc = reg.createNPC(type, player.getName());
        npc.setProtected(false);
        
        TraitCombatLogX clxTrait = npc.getTrait(TraitCombatLogX.class);
        clxTrait.setPlayer(player);
        
        if (cloneInventory) {
            Inventory inv = npc.getTrait(Inventory.class);
            inv.setContents(player.getInventory().getContents());
        }
        
        if (sentinel) {
            SentinelTrait st = npc.getTrait(SentinelTrait.class);
            st.setInvincible(false);
            
            LivingEntity enemy = CombatUtil.getEnemy(player);
            if (enemy != null) {
                UUID enemyID = enemy.getUniqueId();
                st.addTarget(enemyID);
            }
        }
        
        Location loc = player.getLocation();
        npc.spawn(loc);
        
        if (type.isAlive()) {
            LivingEntity npcEntity = (LivingEntity) npc.getEntity();
            npcEntity.setHealth(player.getHealth());
            
            double playerMaxHealth = LegacyHandler.getLegacyHandler().getMaxHealth(player);
            LegacyHandler.getLegacyHandler().setMaxHealth(npcEntity, playerMaxHealth);
        }
    }
    
    private static NPC getNPC(OfflinePlayer op) {
        UUID opUUID = op.getUniqueId();
        
        NPCRegistry reg = CitizensAPI.getNPCRegistry();
        for(NPC npc : reg) {
            if(npc.hasTrait(TraitCombatLogX.class)) {
                TraitCombatLogX clxTrait = npc.getTrait(TraitCombatLogX.class);
                OfflinePlayer npcPlayer = clxTrait.getOfflinePlayer();
                if(npcPlayer != null) {
                    UUID npcUUID = npcPlayer.getUniqueId();
                    if(npcUUID.equals(opUUID)) return npc;
                }
            }
        }
        
        return null;
    }
    
    public static void removeNPC(OfflinePlayer op) {
        NPC playerNPC = getNPC(op);
        if(playerNPC != null) { 
            double health = 0.0D;
            if (playerNPC.isSpawned()) {
                Entity en = playerNPC.getEntity();
                if (en instanceof LivingEntity) {
                    LivingEntity le = (LivingEntity) en;
                    health = le.getHealth();
                }
            }
            
            ConfigData.force(op, "last health", health);
            
            if (health > 0.0D && playerNPC.hasTrait(Inventory.class) && ConfigCitizens.STORE_INVENTORY) {
                Inventory inv = playerNPC.getTrait(Inventory.class);
                List<ItemStack> contents = Util.newList(inv.getContents());
                ConfigData.force(op, "last inventory", contents);
            }
            
            ConfigData.force(op, "punish", true);
            
            playerNPC.despawn(DespawnReason.PLUGIN);
            playerNPC.destroy();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPunish(PlayerPunishEvent e) {
        Player player = e.getPlayer();
        PunishReason reason = e.getReason();
        if (reason != PunishReason.UNKNOWN) {
            if (ConfigCitizens.CANCEL_OTHER_PUNISHMENTS) e.setCancelled(true);
            
            createNPC(player);
            if (ConfigCitizens.SURVIVAL_TIME > 0) SchedulerUtil.runLater(ConfigCitizens.SURVIVAL_TIME * 20L, () -> removeNPC(player));
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onNPCDeath(NPCDeathEvent e) {
        NPC npc = e.getNPC();
        Entity npcEntity = npc.getEntity();
        if(npcEntity instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) npcEntity;
            if(npc.hasTrait(TraitCombatLogX.class)) {
                OfflinePlayer op = npc.getTrait(TraitCombatLogX.class).getOfflinePlayer();
                if(op != null) {
                    if(npc.hasTrait(Inventory.class)) {
                        Inventory inv = npc.getTrait(Inventory.class);
                        final ItemStack[] contents = inv.getContents().clone();
                        
                        final ItemStack[] allAir = new ItemStack[100];
                        Arrays.fill(allAir, new ItemStack(Material.AIR));
                        inv.setContents(allAir);
                        
                        World world = entity.getWorld();
                        Location loc = entity.getLocation().clone();
                        Arrays.stream(contents).forEach(item -> world.dropItem(loc, item));
                    }
                    
                    removeNPC(op);
                }
            }
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        removeNPC(player);
        
        SchedulerUtil.runLater(5L, () -> {
            boolean punish = ConfigData.get(player, "punish", false);
            if (punish) {
                double health = ConfigData.get(player, "last health", player.getHealth());
                player.setHealth(health);
                
                if (ConfigCitizens.STORE_INVENTORY) {
                    List<ItemStack> contents = ConfigData.get(player, "last inventory", Util.newList(player.getInventory().getContents()));
                    ItemStack[] isc = contents.toArray(new ItemStack[0]);
                    player.getInventory().setContents(isc);
                }
                
                ConfigData.force(player, "punish", false);
            }
        });
    }
    
    public static class TraitCombatLogX extends Trait {
        private UUID playerUUID;
        public TraitCombatLogX() {super("combatlogx");}
        
        public void setPlayer(OfflinePlayer op) {
            Validate.notNull(op, "op cannot be NULL!");
            this.playerUUID = op.getUniqueId();
        }
        
        public OfflinePlayer getOfflinePlayer() {
            return Bukkit.getOfflinePlayer(this.playerUUID);
        }
    }
}
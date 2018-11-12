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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import org.mcmonkey.sentinel.SentinelTrait;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Inventory;

public class NPCManager implements Listener {
    public static Map<UUID, Integer> NPC_IDS = Util.newMap();

    private static void createNPC(Player player) {
        if (NPC_IDS.containsKey(player.getUniqueId())) removeNPC(player);

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

        int npcID = npc.getId();
        NPC_IDS.put(player.getUniqueId(), npcID);
    }

    public static void removeNPC(OfflinePlayer op) {
        UUID uuid = op.getUniqueId();
        if (NPC_IDS.containsKey(uuid)) {
            int id = NPC_IDS.get(uuid);
            NPCRegistry reg = CitizensAPI.getNPCRegistry();
            NPC npc = reg.getById(id);

            double health = 0.0D;
            if (npc.isSpawned()) {
                Entity en = npc.getEntity();
                if (en instanceof LivingEntity) {
                    LivingEntity le = (LivingEntity) en;
                    health = le.getHealth();
                }
            }

            ConfigData.force(op, "last health", health);

            if (health > 0.0D && npc.hasTrait(Inventory.class) && ConfigCitizens.STORE_INVENTORY) {
                Inventory inv = npc.getTrait(Inventory.class);
                List<ItemStack> contents = Util.newList(inv.getContents());
                ConfigData.force(op, "last inventory", contents);
            }

            ConfigData.force(op, "punish", true);

            npc.despawn(DespawnReason.PLUGIN);
            npc.destroy();
            NPC_IDS.remove(uuid);
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

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (NPC_IDS.containsKey(uuid)) removeNPC(player);

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
}
package com.SirBlobman.npc.utility;

import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.npc.config.ConfigCitizens;
import com.SirBlobman.npc.config.ConfigData;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.Toggleable;

public class NPCUtil extends Util {
    public static Map<UUID, NPC> NPC_REGISTRY = newMap();
    public static TraitInfo COMBAT_NPC;

    public static void onStartup() {
        TraitInfo ti = TraitInfo.create(CombatNPC.class);
        COMBAT_NPC = ti;
        CitizensAPI.getTraitFactory().registerTrait(ti);
    }

    public static void onShutdown() {
        removeAllNPCs();
        CitizensAPI.getTraitFactory().deregisterTrait(COMBAT_NPC);
    }

    @SuppressWarnings("deprecation")
    public static NPC createNPC(Player p, Location lastLocation) {
        NPCRegistry reg = CitizensAPI.getNPCRegistry();
        UUID uuid = p.getUniqueId();
        String name = p.getName();
        double health = p.getHealth();
        PlayerInventory pi = p.getInventory();
        EntityType type = EntityType.valueOf(ConfigCitizens.OPTION_NPC_ENTITY_TYPE);
        if (type == null || !type.isAlive()) {
            Util.print("Invalid NPC type '" + ConfigCitizens.OPTION_NPC_ENTITY_TYPE + "'. Defaulting to Player");
            type = EntityType.PLAYER;
        }

        NPC npc = reg.createNPC(type, name);
        npc.addTrait(LookClose.class);
        npc.addTrait(CombatNPC.class);
        npc.getTrait(CombatNPC.class).setCombatNPC(true);

        npc.data().set(NPC.DEFAULT_PROTECTED_METADATA, false);
        npc.data().set(NPC.DAMAGE_OTHERS_METADATA, true);
        npc.data().set(NPC.TARGETABLE_METADATA, true);
        if (ConfigCitizens.OPTION_MODIFY_INVENTORIES) {
            npc.addTrait(Inventory.class);
            npc.getTrait(Inventory.class).setContents(pi.getContents());
            npc.data().set(NPC.DROPS_ITEMS_METADATA, true);
            pi.clear();
        }

        npc.spawn(lastLocation);
        LivingEntity le = (LivingEntity) npc.getEntity();
        le.setInvulnerable(false);
        le.setMaxHealth(health);
        le.setHealth(health);

        NPC_REGISTRY.put(uuid, npc);
        runLater(new Runnable() {
            @Override
            public void run() {
                removeNPC(uuid);
            }
        }, ConfigCitizens.OPTION_NPC_SURVIVAL_TIME * 20L);

        return npc;
    }

    public static double getHealth(NPC npc) {
        if (npc == null)
            return 0.0D;
        if (npc.isSpawned()) {
            Entity e = npc.getEntity();
            if (e instanceof LivingEntity) {
                LivingEntity le = (LivingEntity) e;
                double health = le.getHealth();
                return health;
            } else
                return 0.0D;
        } else
            return 0.0D;
    }

    public static void removeNPC(UUID uuid) {
        NPC npc = NPC_REGISTRY.get(uuid);
        if (npc != null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            Location l = npc.getStoredLocation();
            Inventory i = npc.hasTrait(Inventory.class) ? npc.getTrait(Inventory.class) : null;
            double health = getHealth(npc);
            ConfigData.force(op, "health", health);
            ConfigData.force(op, "last location", l);
            if (i != null && ConfigCitizens.OPTION_MODIFY_INVENTORIES) {
                ItemStack[] contents = i.getContents();
                List<ItemStack> list = newList(contents);
                ConfigData.force(op, "inventory", list);
            }

            npc.despawn();
            npc.destroy();
            NPC_REGISTRY.remove(uuid);
        } else
            return;
    }

    @SuppressWarnings("deprecation")
    public static void removeNPC(NPC npc) {
        if (npc != null) {
            String name = npc.getName();
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            Location l = npc.getStoredLocation();
            double health = getHealth(npc);
            Inventory i = npc.hasTrait(Inventory.class) ? npc.getTrait(Inventory.class) : null;

            ConfigData.force(op, "health", health);
            ConfigData.force(op, "last location", l);
            if (i != null && ConfigCitizens.OPTION_MODIFY_INVENTORIES) {
                ItemStack[] contents = i.getContents();
                List<ItemStack> list = newList(contents);
                ConfigData.force(op, "inventory", list);
            }

            npc.despawn();
            npc.destroy();
            NPC_REGISTRY.remove(op.getUniqueId());
        } else
            return;
    }

    public static void removeAllNPCs() {
        for (UUID uuid : newList(NPC_REGISTRY.keySet())) {
            removeNPC(uuid);
        }
    }

    @TraitName("combatlogx_npc")
    public static class CombatNPC extends Trait implements Toggleable {
        @Persist
        private boolean isCombat = false;

        public CombatNPC() {
            super("combatlogx_npc");
        }

        @Override
        public boolean toggle() {
            isCombat = !isCombat;
            return isCombat;
        }

        @Override
        public void run() {
            if (npc.isSpawned() && isCombatNPC()) {
                LivingEntity en = (LivingEntity) npc.getEntity();
                en.setAI(true);
            }
        }

        public boolean isCombatNPC() {
            return isCombat;
        }

        public void setCombatNPC(boolean bool) {
            isCombat = bool;
        }
    }
}
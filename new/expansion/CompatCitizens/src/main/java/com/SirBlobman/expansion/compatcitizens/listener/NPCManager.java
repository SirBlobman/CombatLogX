package com.SirBlobman.expansion.compatcitizens.listener;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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

import com.SirBlobman.combatlogx.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent.PunishReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;
import com.SirBlobman.expansion.compatcitizens.config.ConfigData;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Inventory;

public class NPCManager implements Listener {
	public static Map<UUID, Integer> NPC_IDS = Util.newMap();
	public static NPC createNPC(Player p) {
		if(NPC_IDS.containsKey(p.getUniqueId())) removeNPC(p);

		String entityType = ConfigCitizens.ENTITY_TYPE;
		EntityType type;
		try {
			type = EntityType.valueOf(entityType);
		} catch(Throwable ex) {
			String error = "An error has occured trying to create an NPC: '" + entityType + "' is not valid.\nDefaulting to PLAYER";
			Util.print(error);
			ex.printStackTrace();
			ConfigCitizens.ENTITY_TYPE = EntityType.PLAYER.name();
			type = EntityType.PLAYER;
		}

		boolean cloneInventory = ConfigCitizens.STORE_INVENTORY ? (type == EntityType.PLAYER ? true : false) : false;
		boolean sentinel = ConfigCitizens.USE_SENTINELS ? PluginUtil.isEnabled("Sentinel", "mcmonkey") : false;

		NPCRegistry reg = CitizensAPI.getNPCRegistry();
		NPC npc = reg.createNPC(type, p.getName());

		if(cloneInventory) {
			Inventory inv = npc.getTrait(Inventory.class);
			inv.setContents(p.getInventory().getContents());
		}

		if(sentinel) {
			SentinelTrait st = npc.getTrait(SentinelTrait.class);
			st.setInvincible(false);

			LivingEntity enemy = CombatUtil.getEnemy(p);
			if(enemy != null) {
				UUID enemyID = enemy.getUniqueId();
				st.addTarget(enemyID);
			}
		}

		Location loc = p.getLocation();
		npc.spawn(loc);

		if(type.isAlive()) {
			LivingEntity le = (LivingEntity) npc.getEntity();
			le.setHealth(p.getHealth());

			AttributeInstance pMax = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			AttributeInstance leMax = le.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			leMax.setBaseValue(pMax.getBaseValue());
		}

		int npcID = npc.getId();
		NPC_IDS.put(p.getUniqueId(), npcID);
		return npc;
	}

	public static void removeNPC(OfflinePlayer op) {
		UUID uuid = op.getUniqueId();
		if(NPC_IDS.containsKey(uuid)) {
			int id = NPC_IDS.get(uuid);
			NPCRegistry reg = CitizensAPI.getNPCRegistry();
			NPC npc = reg.getById(id);

			double health = 0.0D;
			if(npc.isSpawned()) {
				Entity en = npc.getEntity();
				if(en instanceof LivingEntity) {
					LivingEntity le = (LivingEntity) en;
					health = le.getHealth();
				}
			}

			ConfigData.force(op, "last health", health);

			if(health > 0.0D && npc.hasTrait(Inventory.class) && ConfigCitizens.STORE_INVENTORY) {
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

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPunish(PlayerPunishEvent e) {
		Player p = e.getPlayer();
		PunishReason reason = e.getReason();
		if(reason != PunishReason.UNKNOWN) {
			if(ConfigCitizens.CANCEL_OTHER_PUNISHMENTS) e.setCancelled(true);

			createNPC(p);		
			if(ConfigCitizens.SURVIVAL_TIME > 0) SchedulerUtil.runLater(ConfigCitizens.SURVIVAL_TIME * 20L, () -> removeNPC(p));
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if(NPC_IDS.containsKey(uuid)) removeNPC(p);
		
		SchedulerUtil.runLater(5L, () -> {
			boolean punish = ConfigData.get(p, "punish", false);
			if(punish) {
				double health = ConfigData.get(p, "last health", p.getHealth());
				p.setHealth(health);

				if(ConfigCitizens.STORE_INVENTORY) {
					List<ItemStack> contents = ConfigData.get(p, "last inventory", Util.newList(p.getInventory().getContents()));
					ItemStack[] isc = contents.toArray(new ItemStack[0]);
					p.getInventory().setContents(isc);
				}
				
				ConfigData.force(p, "punish", false);
			}
		});
	}
}
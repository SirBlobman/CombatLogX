package com.SirBlobman.expansion.worldguard.config;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.olivolja3.force.field.ForceField;
import com.SirBlobman.expansion.worldguard.listener.WorldGuardForceField;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.expansion.NoEntryExpansion.NoEntryMode;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.worldguard.CompatWorldGuard;
import com.SirBlobman.expansion.worldguard.listener.ListenWorldGuard;

public class ConfigWG extends Config {
	private static YamlConfiguration config = new YamlConfiguration();
	public static void load(CompatWorldGuard expansion) {
		File folder = CompatWorldGuard.FOLDER;
		File file = new File(folder, "worldguard.yml");

		if (!file.exists()) copyFromJar("worldguard.yml", folder);
		config = load(file);
		defaults();
		updateMaterials();
		checkValidForceField(expansion);
	}

	public static double NO_ENTRY_KNOCKBACK_STRENGTH;
	public static int MESSAGE_COOLDOWN;
	private static String NO_ENTRY_MODE;
	
	public static boolean FORCEFIELD_ENABLED;
	public static int FORCEFIELD_SIZE;
	private static String FORCEFIELD_MATERIAL_NAME;
	public static Material FORCEFIELD_MATERIAL;
	public static byte FORCEFIELD_MATERIAL_DATA;
	public static String FORCEFIELD_BYPASS_PERMISSION;

	private static void defaults() {
		NO_ENTRY_MODE = get(config, "world guard.no entry.mode", "KNOCKBACK");
		NO_ENTRY_KNOCKBACK_STRENGTH = get(config, "world guard.no entry.knockback power", 1.5D);
		MESSAGE_COOLDOWN = get(config, "world guard.no entry.message cooldown", 5);

		FORCEFIELD_ENABLED = get(config, "world guard.forcefield.enabled", false);
		FORCEFIELD_SIZE = get(config, "world guard.forcefield.size", 4);
		FORCEFIELD_MATERIAL_NAME = get(config, "world guard.forcefield.material", "GLASS");
		FORCEFIELD_MATERIAL = Material.GLASS;
		FORCEFIELD_MATERIAL_DATA = 0;
		FORCEFIELD_BYPASS_PERMISSION = get(config, "world guard.forcefield.bypass permission", "combatlogx.bypass.forcefield");
	}

	public static NoEntryMode getNoEntryMode() {
		if (NO_ENTRY_MODE == null || NO_ENTRY_MODE.isEmpty()) return NoEntryMode.CANCEL;
		
		String mode = NO_ENTRY_MODE.toUpperCase();
		try {
			return NoEntryMode.valueOf(mode);
		} catch (Throwable ex) {
			String error = "Invalid Mode '" + NO_ENTRY_MODE + "' in 'worldguard.yml'. Valid modes are CANCEL, TELEPORT, KNOCKBACK, or KILL";
			Util.print(error);
			return NoEntryMode.CANCEL;
		}
	}

	public static void checkValidForceField(CompatWorldGuard expansion) {
		if(!PluginUtil.isEnabled("CombatLogX")) return;
		Plugin plugin = JavaPlugin.getPlugin(CombatLogX.class);

		if(FORCEFIELD_ENABLED && !PluginUtil.isEnabled("ProtocolLib")) {
			expansion.print("ForceField is enabled but you do not have ProtocolLib installed. Automatically disabling...");
			FORCEFIELD_ENABLED = false;
		}

		if(!FORCEFIELD_ENABLED) {
			List<RegisteredListener> registeredListeners = HandlerList.getRegisteredListeners(plugin);
			for(RegisteredListener rl : registeredListeners) {
				Listener listener = rl.getListener();
				if(listener instanceof WorldGuardForceField) {
					ForceField forceField = (ForceField) listener;
					HandlerList.unregisterAll(forceField);

					forceField.unregisterProtocol();
					Bukkit.getOnlinePlayers().forEach(forceField::removeForceField);
					forceField.clearData();
				}
			}
			return;
		}

		WorldGuardForceField forceField = new WorldGuardForceField(expansion);
		PluginUtil.regEvents(forceField);
		forceField.registerProtocol();
		Bukkit.getOnlinePlayers().forEach(forceField::updateForceField);
	}

	private static void updateMaterials() {
		if(NMS_Handler.getMinorVersion() < 13 && FORCEFIELD_MATERIAL_NAME.contains(":")) {
			String[] materialStrings = FORCEFIELD_MATERIAL_NAME.split(":");
			Material material = Material.getMaterial(materialStrings[0]);
			if (material != null && material.isBlock()) {
				byte data = Byte.valueOf(materialStrings[1]);
				if (data > 15) data = 0;
				FORCEFIELD_MATERIAL_DATA = data;
				FORCEFIELD_MATERIAL = material;
			}
		} else {
			Material forceFieldMaterial = Material.getMaterial(FORCEFIELD_MATERIAL_NAME);
			if (forceFieldMaterial != null && forceFieldMaterial.isBlock()) FORCEFIELD_MATERIAL = forceFieldMaterial;
		}
	}
}
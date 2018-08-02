package com.SirBlobman.expansion.compatcitizens;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;
import com.SirBlobman.expansion.compatcitizens.listener.NPCManager;

public class CompatCitizens implements CLXExpansion {
	public String getUnlocalizedName() {return "CompatCitizens";}
	public String getName() {return "Citizens Compatibility";}
	public String getVersion() {return "13.1";}
	
	public static File FOLDER;
	
	@Override
	public void enable() {
		if(PluginUtil.isEnabled("Citizens")) {
			FOLDER = getDataFolder();
			ConfigCitizens.load();
			PluginUtil.regEvents(new NPCManager());
		} else {
			String error = "Citizens is not installed, removing expansion....";
			print(error);
			Expansions.unloadExpansion(this);
		}
	}
	
	@Override
	public void disable() {
		if(PluginUtil.isEnabled("Citizens")) {
			for(UUID uuid : NPCManager.NPC_IDS.keySet()) {
				OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
				if(op != null) NPCManager.removeNPC(op);
			}
		}
	}
	
	@Override
	public void onConfigReload() {
		if(PluginUtil.isEnabled("Citizens")) {
			ConfigCitizens.load();
		}
	}
}
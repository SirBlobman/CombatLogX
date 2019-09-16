package com.SirBlobman.expansion.helper;

import java.io.File;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.helper.command.CommandCheckPVP;
import com.SirBlobman.expansion.helper.command.CommandTogglePVP;
import com.SirBlobman.expansion.helper.config.ConfigNewbie;
import com.SirBlobman.expansion.helper.listener.ListenNewbieHelper;

public class NewbieHelper implements CLXExpansion {
	public static File FOLDER;
	
	@Override
	public String getName() {
		return "Newbie Helper";
	}
	
	@Override
	public String getUnlocalizedName() {
		return "NewbieHelper";
	}

	@Override
	public String getVersion() {
		return "14.5";
	}
	

	@Override
	public void enable() {
		FOLDER = getDataFolder();
		
		ConfigNewbie.load();

		CombatLogX plugin = CombatLogX.INSTANCE;
		plugin.forceRegisterCommand("togglepvp", CommandTogglePVP.class, "Toggle your ability to pvp.", "/<command>", "pvptoggle");
		plugin.forceRegisterCommand("checkpvp", CommandCheckPVP.class, "Check the pvp status of a player", "/<command> [player]", "pvpcheck");
		PluginUtil.regEvents(new ListenNewbieHelper());
	}

	@Override
	public void disable() {
		
	}

	@Override
	public void onConfigReload() {
		
	}
}
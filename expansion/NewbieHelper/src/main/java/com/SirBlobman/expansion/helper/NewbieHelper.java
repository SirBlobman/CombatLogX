package com.SirBlobman.expansion.helper;

import java.io.File;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
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
		return "14.3";
	}
	

	@Override
	public void enable() {
		FOLDER = getDataFolder();
		
		ConfigNewbie.load();
		
		CombatLogX.INSTANCE.forceRegisterCommand("togglepvp", CommandTogglePVP.class, "Toggle your ability to pvp.", "/<command>", "pvptoggle");
		PluginUtil.regEvents(new ListenNewbieHelper());
	}

	@Override
	public void disable() {
		
	}

	@Override
	public void onConfigReload() {
		
	}
}
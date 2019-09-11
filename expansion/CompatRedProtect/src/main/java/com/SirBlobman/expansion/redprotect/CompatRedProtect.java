package com.SirBlobman.expansion.redprotect;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.expansion.NoEntryExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.redprotect.config.ConfigRedProtect;
import com.SirBlobman.expansion.redprotect.listener.ListenRedProtect;

import java.io.File;

public class CompatRedProtect extends NoEntryExpansion {
	public static File FOLDER;

	@Override
	public String getName() {
		return "RedProtect Compatibility";
	}

	@Override
	public String getUnlocalizedName() {
		return "CompatRedProtect";
	}

	@Override
	public String getVersion() {
		return "14.4";
	}

	@Override
	public boolean canEnable() {
		if(!PluginUtil.isEnabled("RedProtect", "FabioZumbi12")) {
			print("Could not find RedProtect plugin.");
			return false;
		}

		return true;
	}

	@Override
	public void onEnable() {
		FOLDER = getDataFolder();
		ConfigRedProtect.load();

		ListenRedProtect listener = new ListenRedProtect(this);
		PluginUtil.regEvents(listener);
	}

	@Override
	public void onConfigReload() {
		ConfigRedProtect.load();
	}

	@Override
	public double getKnockbackStrength() {
		return ConfigRedProtect.NO_ENTRY_KNOCKBACK_STRENGTH;
	}

	@Override
	public NoEntryMode getNoEntryMode() {
		return ConfigRedProtect.getNoEntryMode();
	}

	@Override
	public String getNoEntryMessage(boolean mobEnemy) {
		String messageKey = "messages.expansions.redprotect compatibility.no entry";
		return ConfigLang.getWithPrefix(messageKey);
	}

	@Override
	public int getNoEntryMessageCooldown() {
		return ConfigRedProtect.MESSAGE_COOLDOWN;
	}
}
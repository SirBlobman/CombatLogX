package com.SirBlobman.expansion.compatfactions;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.expansion.NoEntryExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.compatfactions.config.ConfigFactions;
import com.SirBlobman.expansion.compatfactions.listener.ListenFactions;
import com.SirBlobman.expansion.compatfactions.utility.FactionsUtil;

import java.io.File;

public class CompatFactions extends NoEntryExpansion {
    public static File FOLDER;
    private static FactionsUtil FUTIL;

    public String getVersion() {
        return "14.5";
    }

    public String getUnlocalizedName() {
        return "CompatFactions";
    }

    public String getName() {
        return "Factions Compatibility";
    }

    @Override
    public void onConfigReload() {
        FUTIL = FactionsUtil.getFactionsUtil();
        if (FUTIL != null) {
            FOLDER = getDataFolder();
            ConfigFactions.load();
			ConfigFactions.checkValidForceField(this, FUTIL);
        }
    }

	@Override
	public boolean canEnable() {
		FUTIL = FactionsUtil.getFactionsUtil();
		if(FUTIL == null) {
			print("Could not find a valid Factions plugin.");
			return false;
		}
		
		return true;
	}

	@Override
	public void onEnable() {
		FOLDER = getDataFolder();
		ConfigFactions.load();
		ConfigFactions.checkValidForceField(this, FUTIL);
		
		ListenFactions listener = new ListenFactions(this, FUTIL);
		PluginUtil.regEvents(listener);
	}

	@Override
	public double getKnockbackStrength() {
		return ConfigFactions.NO_ENTRY_KNOCKBACK_STRENGTH;
	}

	@Override
	public NoEntryMode getNoEntryMode() {
		return ConfigFactions.getNoEntryMode();
	}

	@Override
	public String getNoEntryMessage(boolean mobEnemy) {
        String messageKey = "messages.expansions.factions compatibility.no entry";
        return ConfigLang.getWithPrefix(messageKey);
	}

	@Override
	public int getNoEntryMessageCooldown() {
		return ConfigFactions.MESSAGE_COOLDOWN;
	}
}
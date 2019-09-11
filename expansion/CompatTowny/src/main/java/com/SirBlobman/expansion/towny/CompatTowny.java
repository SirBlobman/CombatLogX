package com.SirBlobman.expansion.towny;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.expansion.NoEntryExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.towny.config.ConfigTowny;
import com.SirBlobman.expansion.towny.listener.ListenTowny;

import java.io.File;

public class CompatTowny extends NoEntryExpansion {
    public static File FOLDER;

    public String getUnlocalizedName() {
        return "CompatTowny";
    }

    public String getName() {
        return "Towny Compatibility";
    }

    public String getVersion() {
        return "14.5";
    }
    
    @Override
    public boolean canEnable() {
    	if(!PluginUtil.isEnabled("Towny")) {
    		print("Could not find Towny plugin.");
    		return false;
    	}
    	
    	return true;
    }

    @Override
    public void onEnable() {
    	FOLDER = getDataFolder();
    	ConfigTowny.load();
    	
    	ListenTowny listener = new ListenTowny(this);
    	PluginUtil.regEvents(listener);
    }

    @Override
    public void onConfigReload() {
    	ConfigTowny.load();
    }

	@Override
	public double getKnockbackStrength() {
		return ConfigTowny.NO_ENTRY_KNOCKBACK_STRENGTH;
	}

	@Override
	public NoEntryMode getNoEntryMode() {
		return ConfigTowny.getNoEntryMode();
	}

	@Override
	public String getNoEntryMessage(boolean mobEnemy) {
		String messageKey = "messages.expansions.towny compatibility.no entry";
		return ConfigLang.getWithPrefix(messageKey);
	}

	@Override
	public int getNoEntryMessageCooldown() {
		return ConfigTowny.MESSAGE_COOLDOWN;
	}
}
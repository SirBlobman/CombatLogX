package com.SirBlobman.expansion.residence;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.expansion.NoEntryExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.residence.config.ConfigResidence;
import com.SirBlobman.expansion.residence.listener.ListenResidence;

import java.io.File;

public class CompatResidence extends NoEntryExpansion {
    public static File FOLDER;
    public String getUnlocalizedName() {return "CompatResidence";}
    public String getName() {return "Residence Compatibility";}
    public String getVersion() {return "14.6";}
    
    @Override
    public boolean canEnable() {
    	if(!PluginUtil.isEnabled("Residence", "bekvon")) {
    		print("Could not find Residence plugin.");
    		return false;
    	}
    	
    	return true;
    }
    
    @Override
    public void onEnable() {
    	FOLDER = getDataFolder();
    	ConfigResidence.load();
    	
    	ListenResidence listener = new ListenResidence(this);
    	PluginUtil.regEvents(listener);
    }

    @Override
    public void onConfigReload() {
    	ConfigResidence.load();
    }
    
	@Override
	public double getKnockbackStrength() {
		return ConfigResidence.NO_ENTRY_KNOCKBACK_STRENGTH;
	}
	
	@Override
	public NoEntryMode getNoEntryMode() {
		return ConfigResidence.getNoEntryMode();
	}
	
	@Override
	public String getNoEntryMessage(boolean mobEnemy) {
		String messageKey = "messages.expansions.residence compatibility.no entry";
		return ConfigLang.getWithPrefix(messageKey);
	}
	
	@Override
	public int getNoEntryMessageCooldown() {
		return ConfigResidence.MESSAGE_COOLDOWN;
	}
}

package com.SirBlobman.expansion.worldguard;

import java.io.File;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.expansion.NoEntryExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.worldguard.config.ConfigWG;
import com.SirBlobman.expansion.worldguard.listener.ListenWorldGuard;
import com.SirBlobman.expansion.worldguard.utility.WGUtil;

public class CompatWorldGuard extends NoEntryExpansion {
    public static File FOLDER;
    
    public String getUnlocalizedName() {
        return "CompatWorldGuard";
    }
    
    public String getName() {
        return "WorldGuard Compatibility";
    }
    
    public String getVersion() {
        return "14.11";
    }
    
    @Override
    public Boolean preload() { return true; }
    
    @Override
    public void load() {
        FOLDER = getDataFolder();
        if(Util.PM.getPlugin("WorldGuard") == null) {
            print("WorldGuard is not installed, automatically disabling...");
            Expansions.unloadExpansion(this);
            return;
        }
        
        WGUtil.onLoad();
        ConfigWG.load(this);
    }
    
    @Override
    public boolean canEnable() {
    	if(!PluginUtil.isEnabled("WorldGuard")) {
    		print("Could not find WorldGuard plugin.");
    		return false;
    	}
    	
    	return true;
    }
    
    @Override
    public void onEnable() {
    	WGUtil.onEnable(this);
    	ConfigWG.checkValidForceField(this);
    	
    	ListenWorldGuard listener = new ListenWorldGuard(this);
    	PluginUtil.regEvents(listener);
    }
    
    @Override
    public void onConfigReload() {
    	ConfigWG.load(this);
    }

	@Override
	public double getKnockbackStrength() {
		return ConfigWG.NO_ENTRY_KNOCKBACK_STRENGTH;
	}

	@Override
	public NoEntryMode getNoEntryMode() {
		return ConfigWG.getNoEntryMode();
	}

	@Override
	public String getNoEntryMessage(boolean mobEnemy) {
		String messageKey = "messages.expansions.worldguard compatibility.no entry." + (mobEnemy ? "mob" : "pvp");
		return ConfigLang.getWithPrefix(messageKey);
	}

	@Override
	public int getNoEntryMessageCooldown() {
		return ConfigWG.MESSAGE_COOLDOWN;
	}
}
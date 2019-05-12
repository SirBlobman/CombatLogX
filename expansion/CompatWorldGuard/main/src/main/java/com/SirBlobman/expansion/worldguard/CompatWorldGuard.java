
package com.SirBlobman.expansion.worldguard;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.worldguard.config.ConfigWG;
import com.SirBlobman.expansion.worldguard.listener.ListenWorldGuard;
import com.SirBlobman.expansion.worldguard.utility.WGUtil;

import java.io.File;

public class CompatWorldGuard implements CLXExpansion {
    public static File FOLDER;
    
    public String getUnlocalizedName() {
        return "CompatWorldGuard";
    }
    
    public String getName() {
        return "WorldGuard Compatibility";
    }
    
    public String getVersion() {
        return "14.1";
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
        ConfigWG.load();
    }
    
    @Override
    public void enable() {
        if(!PluginUtil.isEnabled("WorldGuard")) {
            print("WorldGuard is not installed, automatically disabling...");
            Expansions.unloadExpansion(this);
            return;
        }
        
        PluginUtil.regEvents(new ListenWorldGuard());
        WGUtil.onEnable();
        ConfigWG.checkValidForceField();
    }
    
    @Override
    public void disable() {
        
    }
    
    @Override
    public void onConfigReload() {
        if (PluginUtil.isEnabled("WorldGuard")) {
            ConfigWG.load();
        }
    }
}
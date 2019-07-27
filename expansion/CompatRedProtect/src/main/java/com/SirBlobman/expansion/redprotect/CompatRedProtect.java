package com.SirBlobman.expansion.redprotect;

import java.io.File;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.redprotect.config.ConfigRedProtect;
import com.SirBlobman.expansion.redprotect.listener.ListenRedProtect;

public class CompatRedProtect implements CLXExpansion {
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
        return "14.2";
    }
    
    public static File FOLDER;
    
    @Override
    public void enable() {
        if(!PluginUtil.isEnabled("RedProtect", "FabioZumbi12")) {
            Expansions.unloadExpansion(this);
            print("Could not find RedProtect, automatically disabling...");
            return;
        }
        
        FOLDER = getDataFolder();
        ConfigRedProtect.load();
        PluginUtil.regEvents(new ListenRedProtect());
    }

    @Override
    public void disable() {
    	
    }

    @Override
    public void onConfigReload() {
    	if(PluginUtil.isEnabled("RedProtect")) ConfigRedProtect.load();
    }
}
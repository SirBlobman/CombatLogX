package com.SirBlobman.placeholderapi;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;

public class CompatPlaceholderAPI implements CLXExpansion {
    @Override
    public void enable() {
        boolean installed = false;
        if (Util.PM.isPluginEnabled("PlaceholderAPI")) {
            HookP h = new HookP();
            h.register();
            installed = true;
        }
        
        if (Util.PM.isPluginEnabled("MVdWPlaceholderAPI")) {
            HookM h = new HookM();
            h.register();
            installed = true;
        }
        
        if (!installed) {
            String error = "Could not find PlaceholderAPI or MVdWPlaceholderAPI! This expansion is useless.";
            print(error);
        }
    }
    
    public String getUnlocalizedName() {
        return "CompatPlaceholderAPI";
    }
    
    public String getName() {
        return "PlaceholderAPI Compatibility";
    }
    
    public String getVersion() {
        return "3";
    }
    
    @Override
    public void onConfigReload() {
        
    }
}
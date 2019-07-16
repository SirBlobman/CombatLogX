package com.SirBlobman.expansion.lands;

import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.lands.config.ConfigLands;
import com.SirBlobman.expansion.lands.listener.ListenLands;
import com.SirBlobman.expansion.lands.utility.LandsUtil;

import java.io.File;

public class CompatLands implements CLXExpansion, Listener {
    public static File FOLDER;
    
    public String getUnlocalizedName() {
        return "CompatLands";
    }
    
    public String getName() {
        return "Lands Compatibility";
    }
    
    public String getVersion() {
        return "14.2";
    }
    
    public boolean checkForLands(boolean print) {
        if(!PluginUtil.isEnabled("Lands", "Angeschossen")) {
            if(print) print("Could not find plugin 'Lands'. Automatically disabling...");
            return false;
        }
        
        try {
            Class.forName("me.angeschossen.lands.api.landsaddons.LandsAddon");
            return true;
        } catch(ReflectiveOperationException ex) {
            if(print) print("Your lands version does not support the API used by CombatLogX. If you believe this is an error, please contact SirBlobman.");
            return false;
        }
    }
    
    @Override
    public void enable() {
        if(!checkForLands(true)) {
            Expansions.unloadExpansion(this);
            return;
        }
        
        FOLDER = getDataFolder();
        ConfigLands.load();
        PluginUtil.regEvents(new ListenLands());
    }
    
    @Override
    public void disable() {
        if(checkForLands(false)) LandsUtil.onDisable();
    }
    
    @Override
    public void onConfigReload() {
        if(checkForLands(false)) ConfigLands.load();
    }
}
package com.SirBlobman.expansion.residence;

import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.residence.config.ConfigResidence;
import com.SirBlobman.expansion.residence.listener.ListenResidence;

import java.io.File;

public class CompatResidence implements CLXExpansion, Listener {
    public String getUnlocalizedName() {return "CompatResidence";}
    public String getName() {return "Residence Compatibility";}
    public String getVersion() {return "14.2";}
    
    public static File FOLDER;
    
    @Override
    public void enable() {
        if(PluginUtil.isEnabled("Residence", "bekvon")) {
            FOLDER = getDataFolder();
            ConfigResidence.load();
            PluginUtil.regEvents(new ListenResidence());
        } else {
            String error = "Could not find Residence. Automatically disabling...";
            Expansions.unloadExpansion(this);
            print(error);
        }
    }

    @Override
    public void disable() {
        
    }

    @Override
    public void onConfigReload() {
        if(PluginUtil.isEnabled("Residence", "bekvon")) ConfigResidence.load();
    }
}
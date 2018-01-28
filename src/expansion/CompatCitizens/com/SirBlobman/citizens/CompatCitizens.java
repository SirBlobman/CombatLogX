package com.SirBlobman.citizens;

import com.SirBlobman.citizens.config.ConfigData;
import com.SirBlobman.citizens.utility.NPCUtil;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;

import java.io.File;

public class CompatCitizens implements CLXExpansion {
    public static File FOLDER;
    
    @Override
    public void enable() {
        if(PluginUtil.isPluginEnabled("Citizens")) {
            FOLDER = getDataFolder();
            ConfigData.load();
            Util.regEvents(new ListenCitizens());
        } else {
            String error = "Citizens is not installed. This expansion is useless!";
            print(error);
        }
    }
    
    @Override
    public void disable() {
        if(PluginUtil.isPluginEnabled("Citizens")) {
            NPCUtil.removeAllNPCs();
            Util.print("Removed all Combat NPCs");
        }
    }
    
    public String getUnlocalizedName() {return "CompatCitizens";}
    public String getName() {return "Citizens Compatibility";}
    public String getVersion() {return "2.0.0";}
}
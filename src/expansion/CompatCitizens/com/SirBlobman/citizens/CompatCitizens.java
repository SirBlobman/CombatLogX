package com.SirBlobman.citizens;

import com.SirBlobman.citizens.config.ConfigData;
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
            ListenCitizens.removeAllNPCs();
        }
    }
    
    @Override
    public String getName() {return "Citizens Compatibility";}
    
    @Override
    public String getVersion() {return "1.0.2";}
}
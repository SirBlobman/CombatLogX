package com.SirBlobman.citizens;

import com.SirBlobman.citizens.config.ConfigData;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;

import java.io.File;

public class CompatCitizens implements CLXExpansion {
    public static File FOLDER;
    
    @Override
    public void enable() {
        if(Util.PM.isPluginEnabled("Citizens")) {
            FOLDER = getDataFolder();
            ConfigData.load();
            Util.regEvents(new ListenCitizens());
        } else {
            String error = "Citizens is not installed. This expansion is useless!";
            Util.print(error);
        }
    }
    
    @Override
    public String getName() {return "Citizens Compatibility";}
    
    @Override
    public String getVersion() {return "1.0.0";}
}
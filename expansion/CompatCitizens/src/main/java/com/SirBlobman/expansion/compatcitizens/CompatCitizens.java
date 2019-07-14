package com.SirBlobman.expansion.compatcitizens;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;
import com.SirBlobman.expansion.compatcitizens.listener.ListenCreateNPCs;
import com.SirBlobman.expansion.compatcitizens.listener.ListenHandleNPCs;
import com.SirBlobman.expansion.compatcitizens.listener.ListenPlayerJoin;
import com.SirBlobman.expansion.compatcitizens.trait.TraitCombatLogX;

import java.io.File;

public class CompatCitizens implements CLXExpansion {
    public static File FOLDER;
    
    public String getUnlocalizedName() {
        return "CompatCitizens";
    }
    
    public String getName() {
        return "Citizens Compatibility";
    }
    
    public String getVersion() {
        return "14.15";
    }
    
    @Override
    public void enable() {
        if(!PluginUtil.isEnabled("Citizens")) {
            print("Citizens is not installed, automatically disabling...");
            Expansions.unloadExpansion(this);
            return;
        }
        
        FOLDER = getDataFolder();
        ConfigCitizens.load();
        TraitCombatLogX.onEnable();
        PluginUtil.regEvents(new ListenCreateNPCs(this), new ListenPlayerJoin(), new ListenHandleNPCs());
    }
    
    @Override
    public void disable() {
        if(!PluginUtil.isEnabled("Citizens")) return;
        TraitCombatLogX.onDisable();
    }
    
    @Override
    public void onConfigReload() {
        if(!PluginUtil.isEnabled("Citizens")) return;
        
        ConfigCitizens.load();
    }
}
package com.SirBlobman.expansion.citizens;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.citizens.config.ConfigCitizens;
import com.SirBlobman.expansion.citizens.listener.ListenCreateNPCs;
import com.SirBlobman.expansion.citizens.listener.ListenHandleNPCs;
import com.SirBlobman.expansion.citizens.listener.ListenPlayerJoin;
import com.SirBlobman.expansion.citizens.listener.ListenTotemNPC;
import com.SirBlobman.expansion.citizens.trait.TraitCombatLogX;

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
        return "14.23";
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
        
        int minorVersion = NMS_Handler.getMinorVersion();
        if(minorVersion >= 11) PluginUtil.regEvents(new ListenTotemNPC());
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
package com.SirBlobman.expansion.cheatprevention;

import java.io.File;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;
import com.SirBlobman.expansion.cheatprevention.listener.*;

public class CheatPrevention implements CLXExpansion {
    public static File FOLDER;
    
    public String getUnlocalizedName() {
        return "CheatPrevention";
    }
    
    public String getName() {
        return "Cheat Prevention";
    }
    
    public String getVersion() {
        return "14.13";
    }
    
    @Override
    public void enable() {
        FOLDER = getDataFolder();
        ConfigCheatPrevention.load();
        
        PluginUtil.regEvents(new ListenCheatPrevention(), new ListenCommandBlocker(), new ListenFlight());
        
        // The elytra item and related events were added in 1.9+
        int minorVersion = NMS_Handler.getMinorVersion();
        if(minorVersion >= 9) PluginUtil.regEvents(new ListenElytra());
        
        //The Totem of Undying was added in 1.11+
        if(minorVersion >= 11) PluginUtil.regEvents(new ListenTotem());
        
        // EntityPickupItemEvent replaced PlayerPickupItemEvent in 1.12
        if(minorVersion >= 12) PluginUtil.regEvents(new ListenNewItemPickup());
        else PluginUtil.regEvents(new ListenOldItemPickup());
        
        // Riptide added in 1.13
        if(minorVersion >= 13) PluginUtil.regEvents(new ListenRiptide());
    }
    
    @Override
    public void disable() {
        
    }
    
    @Override
    public void onConfigReload() {
        ConfigCheatPrevention.load();
    }
}
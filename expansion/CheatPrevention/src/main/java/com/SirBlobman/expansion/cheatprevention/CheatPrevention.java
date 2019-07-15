package com.SirBlobman.expansion.cheatprevention;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;
import com.SirBlobman.expansion.cheatprevention.listener.ListenCheatPrevention;
import com.SirBlobman.expansion.cheatprevention.listener.ListenCommandBlocker;
import com.SirBlobman.expansion.cheatprevention.listener.ListenElytra;
import com.SirBlobman.expansion.cheatprevention.listener.ListenFlight;
import com.SirBlobman.expansion.cheatprevention.listener.ListenNewItemPickup;
import com.SirBlobman.expansion.cheatprevention.listener.ListenOldItemPickup;
import com.SirBlobman.expansion.cheatprevention.listener.ListenRiptide;
import com.SirBlobman.expansion.cheatprevention.listener.ListenTotem;
import com.SirBlobman.expansion.cheatprevention.olivolja3.AliasDetection;

import java.io.File;
import java.util.List;

public class CheatPrevention implements CLXExpansion {
    public static File FOLDER;
    
    public String getUnlocalizedName() {
        return "CheatPrevention";
    }
    
    public String getName() {
        return "Cheat Prevention";
    }
    
    public String getVersion() {
        return "14.8";
    }
    
    @Override
    public void enable() {
        FOLDER = getDataFolder();
        ConfigCheatPrevention.load();
        
        AliasDetection.cmdDetect();
        detectAliases();
        
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
        detectAliases();
    }
    
    private void detectAliases() {
        List<String> newList = Util.newList();

        List<String> blockedCommandList = Util.newList(ConfigCheatPrevention.BLOCKED_COMMANDS_LIST);
        for(String blockedCmd : blockedCommandList) {
            Util.debug("Checking aliases for command '" + blockedCmd + "'.");
            String noSlash = blockedCmd.startsWith("/") ? blockedCmd.substring(1) : blockedCmd;
            PluginCommand pluginCommand = Bukkit.getPluginCommand(noSlash);
            if(pluginCommand == null) {
                Util.debug("'" + blockedCmd + "' is not a valid command.");
                continue;
            }
            
            String originalCommand = pluginCommand.getName();
            String originalWithSlash = "/" + originalCommand;
            newList.add(originalWithSlash);
            Util.debug("Original command for '" + blockedCmd + "' is '" + originalCommand + "'.");
            
            List<String> aliasList = pluginCommand.getAliases();
            for(String alias : aliasList) {
                Util.debug("Found alias '" + alias + "' for command '" + originalCommand + "'.");
                String aliasWithSlash = "/" + alias;
                newList.add(aliasWithSlash);
            }
        }
        
        ConfigCheatPrevention.BLOCKED_COMMANDS_LIST.addAll(newList);
    }
}
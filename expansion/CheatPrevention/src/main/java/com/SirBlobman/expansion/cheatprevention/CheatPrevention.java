package com.SirBlobman.expansion.cheatprevention;

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
        return "13.10";
    }
    
    @Override
    public void enable() {
        FOLDER = getDataFolder();
        ConfigCheatPrevention.load();
        AliasDetection.cmdDetect();
        
        PluginUtil.regEvents(new ListenCheatPrevention(), new ListenCommandBlocker(), new ListenFlight());
        
        // The elytra item and related events were added in 1.9+
        int majorVersion = NMS_Handler.getMajorVersion();
        if(majorVersion >= 9) PluginUtil.regEvents(new ListenElytra());
        
        // EntityPickupItemEvent replaced PlayerPickupItemEvent in 1.12
        if(majorVersion >= 12) PluginUtil.regEvents(new ListenNewItemPickup());
        else PluginUtil.regEvents(new ListenOldItemPickup());
    }
    
    @Override
    public void disable() {
        
    }
    
    @Override
    public void onConfigReload() {
        ConfigCheatPrevention.load();
        detectAliases();
    }
    
    private static void detectAliases() {
        List<String> list = ConfigCheatPrevention.BLOCKED_COMMANDS_LIST;
        List<String> newList = Util.newList();
        
        list.forEach(blocked -> {
            Util.debug(String.format("Checking aliases for command '%s'", blocked));
            String withoutSlash = blocked.substring(1);
            PluginCommand pcmd = Util.SERVER.getPluginCommand(withoutSlash);
            if (pcmd != null) {
                String withSlash = "/" + pcmd.getName();
                newList.add(withSlash);
                List<String> aliases = pcmd.getAliases();
                aliases.forEach(alias -> {
                    Util.debug(String.format("Found alias '%s' for command '%s'", alias, blocked));
                    String asCmd = "/" + alias;
                    newList.add(asCmd);
                });
            }
        });
        
        ConfigCheatPrevention.BLOCKED_COMMANDS_LIST.addAll(newList);
    }
}
package com.SirBlobman.expansion.cheatprevention;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.legacy.LegacyHandler;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;
import com.SirBlobman.expansion.cheatprevention.listener.ListenCheatPrevention;
import com.SirBlobman.expansion.cheatprevention.listener.ListenElytra;
import com.SirBlobman.expansion.cheatprevention.olivolja3.AliasDetection;

import java.io.File;
import java.util.List;

import org.bukkit.command.PluginCommand;

public class CheatPrevention implements CLXExpansion {
    public static File FOLDER;
    
    public String getUnlocalizedName() {
        return "CheatPrevention";
    }
    
    public String getName() {
        return "Cheat Prevention";
    }
    
    public String getVersion() {
        return "13.3";
    }
    
    @Override
    public void enable() {
        FOLDER = getDataFolder();
        ConfigCheatPrevention.load();
        PluginUtil.regEvents(new ListenCheatPrevention());
        
        // The elytra item and related events were added in 1.9+
        int majorVersion = LegacyHandler.getMajorVersion();
        if(majorVersion >= 9) PluginUtil.regEvents(new ListenElytra());
        
        AliasDetection.cmdDetect();
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
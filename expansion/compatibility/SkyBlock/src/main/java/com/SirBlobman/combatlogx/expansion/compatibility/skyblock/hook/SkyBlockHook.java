package com.SirBlobman.combatlogx.expansion.compatibility.skyblock.hook;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.expansion.compatibility.skyblock.CompatibilitySkyBlock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

public abstract class SkyBlockHook {
    public abstract boolean doesTeamMatch(Player player1, Player player2);
    public abstract Object getIslandFor(Player player);
    public abstract Object getIslandAt(Location location);

    public Object getIslandAt(Entity entity) {
        if(entity == null) return null;

        Location location = entity.getLocation();
        return getIslandAt(location);
    }

    private static SkyBlockHook SKYBLOCK_HOOK = null;
    public static SkyBlockHook getSkyBlockHook(CompatibilitySkyBlock expansion) {
        if(SKYBLOCK_HOOK != null) return SKYBLOCK_HOOK;

        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = expansion.getLogger();

        if(manager.isPluginEnabled("BentoBox")) {
            Plugin plugin = manager.getPlugin("BentoBox");
            if(plugin != null) {
                PluginDescriptionFile description = plugin.getDescription();
                String version = description.getVersion();
                logger.info("Checking 'BentoBox v" + version + "' for BSkyBlock");
                
                if(HookBentoBox.hookIntoBSkyBlock(logger)) {
                    SKYBLOCK_HOOK = new HookBSkyBlock();
                    return getSkyBlockHook(expansion);
                }
            }
        }
    
        if(manager.isPluginEnabled("ASkyBlock")) {
            printHookInfo(expansion, "ASkyBlock");
            SKYBLOCK_HOOK = new HookASkyBlock();
            return getSkyBlockHook(expansion);
        }

        if(manager.isPluginEnabled("FabledSkyBlock")) {
            printHookInfo(expansion, "FabledSkyBlock");
            SKYBLOCK_HOOK = new HookFabledSkyBlock();
            return getSkyBlockHook(expansion);
        }
        
        if(manager.isPluginEnabled("SuperiorSkyblock2")) {
            printHookInfo(expansion, "SuperiorSkyblock2");
            SKYBLOCK_HOOK = new HookSuperiorSkyBlock2();
            return getSkyBlockHook(expansion);
        }

        if(manager.isPluginEnabled("uSkyBlock")) {
            printHookInfo(expansion, "uSkyBlock");
            SKYBLOCK_HOOK = new HookUltimateSkyBlock();
            return getSkyBlockHook(expansion);
        }

        return null;
    }
    
    private static void printHookInfo(CompatibilitySkyBlock expansion, String pluginName) {
        PluginManager manager = Bukkit.getPluginManager();
        if(!manager.isPluginEnabled(pluginName)) return;
        
        Plugin plugin = manager.getPlugin(pluginName);
        if(plugin == null) return;
    
        PluginDescriptionFile description = plugin.getDescription();
        String fullName = description.getFullName();
        
        Logger logger = expansion.getLogger();
        logger.info("Successfully hooked into " + fullName);
    }
}
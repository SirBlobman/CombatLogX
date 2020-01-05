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

        if(manager.isPluginEnabled("ASkyBlock")) {
            Plugin plugin = manager.getPlugin("ASkyBlock");
            if(plugin == null) return null;

            PluginDescriptionFile description = plugin.getDescription();
            String version = description.getVersion();

            SKYBLOCK_HOOK = new HookASkyBlock();
            logger.info("Successfully hooked into ASkyBlock v" + version);
            return getSkyBlockHook(expansion);
        }

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

        if(manager.isPluginEnabled("FabledSkyBlock")) {
            Plugin plugin = manager.getPlugin("FabledSkyBlock");
            if(plugin == null) return null;

            PluginDescriptionFile description = plugin.getDescription();
            String version = description.getVersion();

            SKYBLOCK_HOOK = new HookFabledSkyBlock();
            logger.info("Successfully hooked into FabledSkyBlock v" + version);
            return getSkyBlockHook(expansion);
        }

        if(manager.isPluginEnabled("uSkyBlock")) {
            Plugin plugin = manager.getPlugin("uSkyBlock");
            if(plugin == null) return null;

            PluginDescriptionFile description = plugin.getDescription();
            String version = description.getVersion();

            SKYBLOCK_HOOK = new HookUltimateSkyBlock();
            logger.info("Successfully hooked into uSkyBlock v" + version);
            return getSkyBlockHook(expansion);
        }

        return null;
    }
}
package com.SirBlobman.combatlogx.expansion.compatibility.skyblock.hook;

import java.util.Optional;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.AddonDescription;
import world.bentobox.bentobox.managers.AddonsManager;

public final class HookBentoBox {
    public static boolean hookIntoBSkyBlock(Logger logger) {
        Addon bSkyBlock = getBSkyBlock();
        if(bSkyBlock == null) {
            logger.info("Could not find BSkyBlock in BentoBox.");
            return false;
        }
        
        AddonDescription description = bSkyBlock.getDescription();
        String version = description.getVersion();
        
        logger.info("Successfully hooked into BSkyBlock v" + version);
        return true;
    }
    
    public static Addon getBSkyBlock() {
        BentoBox bentoBox = JavaPlugin.getPlugin(BentoBox.class);
        AddonsManager manager = bentoBox.getAddonsManager();
        
        Optional<Addon> addon = manager.getAddonByName("BentoBox");
        return addon.orElse(null);
    }
}
package com.SirBlobman.combatlogx.expansion.compatibility.griefprevention;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryForceFieldListener;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryListener;
import com.SirBlobman.combatlogx.expansion.compatibility.griefprevention.handler.GriefPreventionNoEntryHandler;

public class CompatibilityGriefPrevention extends NoEntryExpansion {
    private NoEntryForceFieldListener forceFieldListener;
    private NoEntryHandler noEntryHandler;
    public CompatibilityGriefPrevention(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public boolean canEnable() {
        Logger logger = getLogger();
        PluginManager manager = Bukkit.getPluginManager();
        Plugin pluginGriefPrevention = manager.getPlugin("GriefPrevention");
        if(pluginGriefPrevention == null || !pluginGriefPrevention.isEnabled()) {
            logger.info("The GriefPrevention plugin could not be found.");
            return false;
        }

        String version = pluginGriefPrevention.getDescription().getVersion();
        logger.info("Successfully hooked into GriefPrevention v" + version);
        return true;
    }

    @Override
    public void onActualEnable() {
        Logger logger = getLogger();
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();

        saveDefaultConfig("griefprevention-compatibility.yml");
        this.noEntryHandler = new GriefPreventionNoEntryHandler(this);

        NoEntryListener listener = new NoEntryListener(this);
        expansionManager.registerListener(this, listener);

        PluginManager manager = Bukkit.getPluginManager();
        Plugin pluginProtocolLib = manager.getPlugin("ProtocolLib");
        if(pluginProtocolLib != null) {
            forceFieldListener = new NoEntryForceFieldListener(this);
            expansionManager.registerListener(this, forceFieldListener);

            String versionProtocolLib = pluginProtocolLib.getDescription().getVersion();
            logger.info("Successfully hooked into ProtocolLib v" + versionProtocolLib);
        }
    }
    
    @Override
    public void onActualDisable() {
        if(forceFieldListener != null)
            forceFieldListener.unregisterProtocol();
    }
    
    @Override
    public void reloadConfig() {
        reloadConfig("griefprevention-compatibility.yml");
    }

    @Override
    public NoEntryHandler getNoEntryHandler() {
        return this.noEntryHandler;
    }
}
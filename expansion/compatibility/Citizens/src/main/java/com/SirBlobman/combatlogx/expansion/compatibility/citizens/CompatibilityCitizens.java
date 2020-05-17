package com.SirBlobman.combatlogx.expansion.compatibility.citizens;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.shaded.nms.VersionUtil;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener.*;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.manager.NPCManager;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.manager.SentinelManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

public class CompatibilityCitizens extends Expansion {
    private NPCManager npcManager = null;
    private SentinelManager sentinelManager = null;
    public CompatibilityCitizens(ICombatLogX plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad() {
        saveDefaultConfig("citizens-compatibility.yml");
    }
    
    @Override
    public void onEnable() {
        Logger logger = getLogger();
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        
        if(checkForCitizens()) {
            logger.info("Could not find the Citizens plugin.");
            logger.info("This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }
        
        this.npcManager = new NPCManager(this);
        this.npcManager.registerTrait();
        
        if(checkForSentinel()) {
            this.sentinelManager = new SentinelManager(this);
            this.sentinelManager.onEnable();
        }
        
        expansionManager.registerListener(this, new ListenerCombat(this));
        expansionManager.registerListener(this, new ListenerDamageDeath(this));
        expansionManager.registerListener(this, new ListenerLogin(this));
        expansionManager.registerListener(this, new ListenerPunish(this));
        int minorVersion = VersionUtil.getMinorVersion();
        
        // 1.11+ Totem of Undying
        if(minorVersion >= 11) expansionManager.registerListener(this, new ListenerResurrect(this));
    }
    
    @Override
    public void onDisable() {
        if(this.npcManager == null) return;
        this.npcManager.onDisable();
    }
    
    @Override
    public void reloadConfig() {
        reloadConfig("citizens-compatibility.yml");
    }
    
    public NPCManager getNPCManager() {
        return this.npcManager;
    }
    
    public SentinelManager getSentinelManager() {
        return this.sentinelManager;
    }
    
    private boolean checkForCitizens() {
        PluginManager manager = Bukkit.getPluginManager();
        if(!manager.isPluginEnabled("Citizens")) return true;
        
        Plugin plugin = manager.getPlugin("Citizens");
        if(plugin == null) return true;
    
        PluginDescriptionFile description = plugin.getDescription();
        String fullName = description.getFullName();
        
        Logger logger = getLogger();
        logger.info("Successfully hooked into " + fullName);
        return false;
    }
    
    private boolean checkForSentinel() {
        PluginManager manager = Bukkit.getPluginManager();
        if(!manager.isPluginEnabled("Sentinel")) return false;
    
        Plugin plugin = manager.getPlugin("Sentinel");
        if(plugin == null) return false;
    
        PluginDescriptionFile description = plugin.getDescription();
        String fullName = description.getFullName();
    
        Logger logger = getLogger();
        logger.info("Successfully hooked into " + fullName);
        return true;
    }
}
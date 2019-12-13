package com.SirBlobman.combatlogx.expansion.compatibility.worldguard;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.NoEntryExpansion;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookForceField;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.listener.ListenerWorldGuard;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompatibilityWorldGuard extends NoEntryExpansion {
    public CompatibilityWorldGuard(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "CompatibilityWorldGuard";
    }

    @Override
    public String getName() {
        return "WorldGuard Compatibility";
    }

    @Override
    public String getVersion() {
        return "15.0";
    }

    @Override
    public void onLoad() {
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        Plugin pluginWorldGuard = manager.getPlugin("WorldGuard");
        if(pluginWorldGuard == null) {
            logger.info("The WorldGuard plugin could not be found. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        logger.info("Registering custom WorldGuard flags...");
        HookWorldGuard.registerFlags(this);
    }

    @Override
    public boolean canEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        return manager.isPluginEnabled("WorldGuard");
    }

    @Override
    public void onActualEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        Plugin pluginWorldGuard = manager.getPlugin("WorldGuard");
        if(pluginWorldGuard == null) {
            logger.info("The WorldGuard plugin could not be found. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        Plugin pluginProtocolLib = manager.getPlugin("ProtocolLib");
        if(pluginProtocolLib != null) {
            String version = pluginProtocolLib.getDescription().getVersion();
            logger.info("Sucessfully hooked into ProtocolLib v" + version);
        }

        String version = pluginWorldGuard.getDescription().getVersion();
        logger.info("Successfully hooked into WorldGuard v" + version);

        saveDefaultConfig("worldguard-compatibility.yml");

        ListenerWorldGuard listener = new ListenerWorldGuard(this);
        JavaPlugin plugin = getPlugin().getPlugin();
        manager.registerEvents(listener, plugin);

        HookWorldGuard.registerListeners(this);
        HookForceField.checkValidForceField(this);
    }

    @Override
    public void reloadConfig() {
        reloadConfig("worldguard-compatibility.yml");
        HookForceField.checkValidForceField(this);
    }

    @Override
    public double getNoEntryKnockbackStrength() {
        FileConfiguration config = getConfig("worldguard-compatibility.yml");
        return config.getDouble("no-entry.knockback-strength", 1.5D);
    }

    @Override
    public NoEntryMode getNoEntryMode() {
        FileConfiguration config = getConfig("worldguard-compatibility.yml");
        String modeString = config.getString("no-entry.knockback-strength", "KNOCKBACK");

        try {return NoEntryMode.valueOf(modeString);}
        catch(IllegalArgumentException | NullPointerException ex) {return NoEntryMode.KNOCKBACK;}
    }

    @Override
    public String getNoEntryMessage(PlayerPreTagEvent.TagType tagType) {
        String path = "worldguard-compatibility.no-entry.";
        return (path + (tagType == PlayerPreTagEvent.TagType.PLAYER ? "pvp" : "mob"));
    }

    @Override
    public int getNoEntryMessageCooldown() {
        FileConfiguration config = getConfig("worldguard-compatibility.yml");
        return config.getInt("no-entry.message-cooldown", 30);
    }
}
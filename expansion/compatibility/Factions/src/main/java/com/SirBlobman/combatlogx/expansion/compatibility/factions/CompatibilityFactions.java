package com.SirBlobman.combatlogx.expansion.compatibility.factions;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.NoEntryExpansion;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.hook.FactionsHook;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.hook.HookForceField;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.listener.ListenerFactions;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompatibilityFactions extends NoEntryExpansion {
    public CompatibilityFactions(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "CompatibilityFactions";
    }

    @Override
    public String getName() {
        return "Factions Compatibility";
    }

    @Override
    public String getVersion() {
        return "15.0";
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public boolean canEnable() {
        Logger logger = getLogger();
        FactionsHook factionsHook = FactionsHook.getFactionsHook();
        if(factionsHook == null) {
            logger.info("Could not find a Factions plugin to hook into.");
            logger.info("Automatically disabling...");
            ExpansionManager.unloadExpansion(this);
            return false;
        }

        return true;
    }

    @Override
    public void onActualEnable() {
        Logger logger = getLogger();
        PluginManager manager = Bukkit.getPluginManager();

        Plugin pluginProtocolLib = manager.getPlugin("ProtocolLib");
        if(pluginProtocolLib != null) {
            String version = pluginProtocolLib.getDescription().getVersion();
            logger.info("Sucessfully hooked into ProtocolLib v" + version);
        }

        saveDefaultConfig("factions-compatibility.yml");
        HookForceField.onConfigLoad(this);

        FactionsHook factionsHook = FactionsHook.getFactionsHook();
        HookForceField.checkValidForceField(this, factionsHook);

        ListenerFactions listener = new ListenerFactions(this, factionsHook);
        JavaPlugin plugin = getPlugin().getPlugin();
        manager.registerEvents(listener, plugin);
    }

    @Override
    public void reloadConfig() {
        reloadConfig("factions-compatibility.yml");

        Logger logger = getLogger();
        FactionsHook factionsHook = FactionsHook.getFactionsHook();
        if(factionsHook == null) {
            logger.info("Could not find a Factions plugin to hook into.");
            logger.info("Automatically disabling...");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        HookForceField.onConfigLoad(this);
        HookForceField.checkValidForceField(this, factionsHook);
    }

    @Override
    public NoEntryMode getNoEntryMode() {
        FileConfiguration config = getConfig("factions-compatibility.yml");
        String modeString = config.getString("no-entry.knockback-strength", "KNOCKBACK");

        try {return NoEntryMode.valueOf(modeString);}
        catch(IllegalArgumentException | NullPointerException ex) {return NoEntryMode.KNOCKBACK;}
    }

    @Override
    public double getNoEntryKnockbackStrength() {
        FileConfiguration config = getConfig("factions-compatibility.yml");
        return config.getDouble("no-entry.knockback-strength");
    }

    @Override
    public String getNoEntryMessage(TagType tagType) {
        return "factions-compatibility-no-entry";
    }

    @Override
    public int getNoEntryMessageCooldown() {
        FileConfiguration config = getConfig("factions-compatibility.yml");
        return config.getInt("no-entry.message-cooldown", 30);
    }
}
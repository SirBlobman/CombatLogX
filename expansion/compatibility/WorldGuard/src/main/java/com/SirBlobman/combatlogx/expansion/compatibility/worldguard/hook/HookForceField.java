package com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.SirBlobman.combatlogx.api.olivolja3.force.field.ForceField;
import com.SirBlobman.combatlogx.api.shaded.nms.NMS_Handler;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.CompatibilityWorldGuard;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.listener.ListenerForceField;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class HookForceField {
    private static boolean forceFieldEnabled = false;
    private static int forceFieldRadius = 2;
    private static String forceFieldMaterialString = "AIR";
    private static Material forceFieldMaterial = Material.AIR;
    private static byte forceFieldMaterialData = 0;
    private static String forceFieldBypassPermission = "combatlogx.bypass.force.field";

    private static void fixMaterial() {
        int minorVersion = NMS_Handler.getMinorVersion();
        if(minorVersion < 13 && forceFieldMaterialString.contains(":")) {
            String[] split = forceFieldMaterialString.split(Pattern.quote(":"));

            String materialString = split[0];
            Material material = Material.matchMaterial(materialString);
            if(material != null && material.isBlock()) {
                String dataString = split[1];
                byte data = Byte.parseByte(dataString);
                if(data > 15) data = 0;

                forceFieldMaterial = material;
                forceFieldMaterialData = data;
            }
        } else {
            Material material = Material.matchMaterial(forceFieldMaterialString);
            if(material != null && material.isBlock()) forceFieldMaterial = material;
        }
    }

    public static void onConfigLoad(CompatibilityWorldGuard expansion) {
        FileConfiguration config = expansion.getConfig("worldguard-compatibility.yml");
        forceFieldEnabled = config.getBoolean("force-field.enabled");
        forceFieldRadius = config.getInt("force-field.radius");
        forceFieldMaterialString = config.getString("force-field.material");
        forceFieldBypassPermission = config.getString("force-field.bypass-permission");

        fixMaterial();
    }

    public static void checkValidForceField(CompatibilityWorldGuard expansion) {
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = expansion.getLogger();

        if(!manager.isPluginEnabled("CombatLogX")) return;
        JavaPlugin plugin = expansion.getPlugin().getPlugin();

        if(forceFieldEnabled && !manager.isPluginEnabled("ProtocolLib")) {
            logger.info("ForceField is enabled, but ProtocolLib is not installed. Automatically disabling...");
            forceFieldEnabled = false;
        }

        if(!forceFieldEnabled) {
            List<RegisteredListener> registeredListenerList = HandlerList.getRegisteredListeners(plugin);
            for(RegisteredListener registeredListener : registeredListenerList) {
                Listener listener = registeredListener.getListener();
                if(!(listener instanceof ListenerForceField)) continue;

                ForceField forceField = (ForceField) listener;
                HandlerList.unregisterAll(forceField);

                forceField.unregisterProtocol();
                Bukkit.getOnlinePlayers().forEach(forceField::removeForceField);
                forceField.clearData();
            }
        }

        ListenerForceField forceField = new ListenerForceField(expansion);
        manager.registerEvents(forceField, plugin);
        forceField.registerProtocol();
        Bukkit.getOnlinePlayers().forEach(forceField::updateForceField);
    }

    public static int getForceFieldRadius() {
        return forceFieldRadius;
    }

    public static String getBypassPermission() {
        return forceFieldBypassPermission;
    }

    public static Material getForceFieldMaterial() {
        return forceFieldMaterial;
    }

    public static byte getForceFieldMaterialData() {
        return forceFieldMaterialData;
    }
}
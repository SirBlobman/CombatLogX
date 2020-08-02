package com.SirBlobman.combatlogx.api.expansion.noentry;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import com.SirBlobman.api.nms.VersionUtil;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class NoEntryHandler {
    private final NoEntryExpansion expansion;
    public NoEntryHandler(NoEntryExpansion expansion) {
        this.expansion = expansion;
    }

    public NoEntryExpansion getExpansion() {
        return this.expansion;
    }

    public NoEntryMode getNoEntryMode() {
        String fileName = getConfigFileName();
        FileConfiguration config = this.expansion.getConfig(fileName);
        String modeString = config.getString("no-entry.mode", NoEntryMode.KNOCKBACK.name());

        try {return NoEntryMode.valueOf(modeString);}
        catch(IllegalArgumentException | NullPointerException ex) {return NoEntryMode.KNOCKBACK;}
    }

    public double getNoEntryKnockbackStrength() {
        String fileName = getConfigFileName();
        FileConfiguration config = this.expansion.getConfig(fileName);

        return config.getDouble("no-entry.knockback-strength", 1.5D);
    }

    public int getNoEntryMessageCooldown() {
        String fileName = getConfigFileName();
        FileConfiguration config = this.expansion.getConfig(fileName);

        return config.getInt("no-entry.message-cooldown", 30);
    }
    
    public boolean isForceFieldEnabled() {
        String fileName = getConfigFileName();
        FileConfiguration config = this.expansion.getConfig(fileName);
    
        return config.getBoolean("force-field.enabled");
    }

    public String getForceFieldMaterialString() {
        String fileName = getConfigFileName();
        FileConfiguration config = this.expansion.getConfig(fileName);

        return config.getString("force-field.material", "GLASS");
    }

    public int getForceFieldRadius() {
        String fileName = getConfigFileName();
        FileConfiguration config = this.expansion.getConfig(fileName);

        return config.getInt("force-field.radius", 5);
    }

    public String getForceFieldBypassPermission() {
        String fileName = getConfigFileName();
        FileConfiguration config = this.expansion.getConfig(fileName);

        return config.getString("force-field.bypass-permission", "combatlogx.bypass.force.field");
    }

    public Material getForceFieldMaterial() {
        String materialString = getForceFieldMaterialString();

        int minorVersion = VersionUtil.getMinorVersion();
        if(minorVersion >= 13) {
            try {
                Class<?> classMaterial = Class.forName("org.bukkit.Material");
                Method method_matchMaterial = classMaterial.getDeclaredMethod("matchMaterial", String.class, Boolean.TYPE);
                return (Material) method_matchMaterial.invoke(null, materialString, false);
            } catch(ReflectiveOperationException ex) {
                return Material.matchMaterial(materialString);
            }
        }

        if(materialString.contains(":")) {
            String[] split = materialString.split(Pattern.quote(":"));
            materialString = split[0];
        }

        return Material.matchMaterial(materialString);
    }

    public byte getForceFieldMaterialData() {
        String materialString = getForceFieldMaterialString();

        int minorVersion = VersionUtil.getMinorVersion();
        if(minorVersion >= 13) return 0;

        if(materialString.contains(":")) {
            String[] split = materialString.split(Pattern.quote(":"));
            materialString = split[1];

            try {
                return Byte.parseByte(materialString);
            } catch(NumberFormatException ignored) {}
        }

        return 0;
    }

    public boolean canBypassForceField(Player player) {
        String permission = getForceFieldBypassPermission();
        if(permission == null || permission.isEmpty()) return false;

        return player.hasPermission(permission);
    }

    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        return isSafeZone(player, location);
    }

    public abstract String getConfigFileName();
    public abstract String getNoEntryMessagePath(TagType tagType);
    public abstract boolean isSafeZone(Player player, Location location);
}
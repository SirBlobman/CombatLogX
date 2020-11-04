package com.SirBlobman.combatlogx.api.expansion.region;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.SirBlobman.api.configuration.ConfigurationManager;
import com.SirBlobman.api.xseries.XMaterial;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.force.field.ForceFieldListener;
import com.SirBlobman.combatlogx.api.object.TagType;

public class RegionForceField extends ForceFieldListener {
    public RegionForceField(RegionExpansion expansion) {
        super(expansion);
    }

    @Override
    public final boolean isEnabled() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("enabled");
    }

    @Override
    public boolean isSafe(Location location, Player player, TagType tagType) {
        RegionExpansion expansion = getExpansion();
        RegionHandler regionHandler = expansion.getRegionHandler();
        return regionHandler.isSafeZone(player, location, tagType);
    }

    @Override
    public boolean isSafe(Location location, Player player) {
        return isSafe(location, player, TagType.UNKNOWN);
    }

    @Override
    public boolean canBypass(Player player) {
        YamlConfiguration configuration = getConfiguration();
        String bypassPermissionName = configuration.getString("bypass-permission");
        if(bypassPermissionName == null || bypassPermissionName.isEmpty()) return false;

        Permission bypassPermission = new Permission(bypassPermissionName, "CombatLogX Bypass Permission (Force Field)", PermissionDefault.FALSE);
        return player.hasPermission(bypassPermission);
    }

    @Override
    public XMaterial getForceFieldMaterial() {
        YamlConfiguration configuration = getConfiguration();
        String materialName = configuration.getString("material");
        if(materialName == null) return XMaterial.AIR;

        Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(materialName);
        return optionalXMaterial.orElse(XMaterial.AIR);
    }

    @Override
    public int getForceFieldRadius() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getInt("radius");
    }

    private YamlConfiguration getConfiguration() {
        RegionExpansion expansion = getExpansion();
        ICombatLogX plugin = expansion.getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        return configurationManager.get("force-field.yml");
    }
}
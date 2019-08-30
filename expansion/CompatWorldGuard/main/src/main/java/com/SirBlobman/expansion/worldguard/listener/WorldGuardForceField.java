package com.SirBlobman.expansion.worldguard.listener;

import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.expansion.NoEntryExpansion;
import com.SirBlobman.combatlogx.olivolja3.force.field.ForceField;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.expansion.worldguard.CompatWorldGuard;
import com.SirBlobman.expansion.worldguard.config.ConfigWG;
import com.SirBlobman.expansion.worldguard.utility.WGUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WorldGuardForceField extends ForceField {
    public WorldGuardForceField(CompatWorldGuard expansion) {super(expansion);}

    @Override
    public boolean isSafe(Location location, Player player, PlayerTagEvent.TagType tagType) {
        switch(tagType) {
            case PLAYER: return !WGUtil.allowsPvP(location);
            case MOB: return !WGUtil.allowsMobCombat(location);
            default: return false;
        }
    }

    @Override
    public boolean isSafe(Location location, Player player) {
        LivingEntity enemy = CombatUtil.getEnemy(player);
        PlayerTagEvent.TagType tagType = (enemy == null ? PlayerTagEvent.TagType.UNKNOWN : (enemy instanceof Player ? PlayerTagEvent.TagType.PLAYER : PlayerTagEvent.TagType.MOB));
        return isSafe(location, player, tagType);
    }

    @Override
    public boolean canBypass(Player player) {
        return player.hasPermission(ConfigWG.FORCEFIELD_BYPASS_PERMISSION);
    }

    @Override
    public Material getForceFieldMaterial() {
        return ConfigWG.FORCEFIELD_MATERIAL;
    }

    @Override
    public int getForceFieldMaterialData() {
        return ConfigWG.FORCEFIELD_MATERIAL_DATA;
    }

    @Override
    public int getForceFieldRadius() {
        return ConfigWG.FORCEFIELD_SIZE;
    }
}
package com.SirBlobman.combatlogx.expansion.compatibility.worldguard.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.olivolja3.force.field.ForceField;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.CompatibilityWorldGuard;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookForceField;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ListenerForceField extends ForceField {
    public ListenerForceField(CompatibilityWorldGuard expansion) {
        super(expansion);
    }

    @Override
    public boolean isSafe(Location location, Player player, PlayerPreTagEvent.TagType tagType) {
        switch(tagType) {
            case PLAYER: return !HookWorldGuard.allowsPVP(location);
            case MOB: return !HookWorldGuard.allowsMobCombat(location);
            default: return false;
        }
    }

    @Override
    public boolean isSafe(Location location, Player player) {
        ICombatLogX plugin = getExpansion().getPlugin();
        ICombatManager manager = plugin.getCombatManager();

        LivingEntity enemy = manager.getEnemy(player);
        PlayerPreTagEvent.TagType tagType = (enemy == null ? PlayerPreTagEvent.TagType.UNKNOWN : (enemy instanceof Player ? PlayerPreTagEvent.TagType.PLAYER : PlayerPreTagEvent.TagType.MOB));
        return isSafe(location, player, tagType);
    }

    @Override
    public boolean canBypass(Player player) {
        String permission = HookForceField.getBypassPermission();
        return player.hasPermission(permission);
    }

    @Override
    public Material getForceFieldMaterial() {
        return HookForceField.getForceFieldMaterial();
    }

    @Override
    public int getForceFieldMaterialData() {
        return HookForceField.getForceFieldMaterialData();
    }

    @Override
    public int getForceFieldRadius() {
        return HookForceField.getForceFieldRadius();
    }
}

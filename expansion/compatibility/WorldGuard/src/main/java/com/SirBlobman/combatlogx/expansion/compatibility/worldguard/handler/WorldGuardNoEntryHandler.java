package com.SirBlobman.combatlogx.expansion.compatibility.worldguard.handler;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.CompatibilityWorldGuard;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class WorldGuardNoEntryHandler extends NoEntryHandler {
    public WorldGuardNoEntryHandler(CompatibilityWorldGuard expansion) {
        super(expansion);
    }

    @Override
    public String getConfigFileName() {
        return "worldguard-compatibility.yml";
    }

    @Override
    public String getNoEntryMessagePath(TagType tagType) {
        String path = (tagType == TagType.PLAYER ? "pvp" : "mob");
        return ("worldguard-compatibility-no-entry." + path);
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        switch(tagType) {
            case PLAYER: return !HookWorldGuard.allowsPVP(location);
            case MOB: return !HookWorldGuard.allowsMobCombat(location);

            default: return false;
        }
    }

    @Override
    public boolean isSafeZone(Player player, Location location) {
        ICombatLogX plugin = getExpansion().getPlugin();
        ICombatManager manager = plugin.getCombatManager();

        LivingEntity enemy = manager.getEnemy(player);
        TagType tagType = (enemy == null ? TagType.UNKNOWN : (enemy instanceof Player ? TagType.PLAYER : TagType.MOB));
        return isSafeZone(player, location, tagType);
    }
}
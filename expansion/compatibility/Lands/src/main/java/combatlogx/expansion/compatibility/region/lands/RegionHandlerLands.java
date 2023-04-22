package combatlogx.expansion.compatibility.region.lands;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.Area;

public final class RegionHandlerLands extends RegionHandler<LandsExpansion> {
    private LandsIntegration landsIntegration;

    public RegionHandlerLands(@NotNull LandsExpansion expansion) {
        super(expansion);
        this.landsIntegration = null;
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.lands.no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        Entity enemy = tag.getCurrentEnemy();
        TagType tagType = tag.getCurrentTagType();
        RoleFlag roleFlag = getRoleFlag(tagType, enemy);
        if (roleFlag == null) {
            return false;
        }

        Area area = getArea(location);
        if (area == null) {
            return false;
        }


        UUID playerId = player.getUniqueId();
        if (tagType == TagType.DAMAGE) {
            return area.hasRoleFlag(playerId, roleFlag);
        } else {
            return !area.hasRoleFlag(playerId, roleFlag);
        }
    }

    private @NotNull LandsIntegration getLandsIntegration() {
        if (this.landsIntegration == null) {
            LandsExpansion expansion = getExpansion();
            ICombatLogX plugin = expansion.getPlugin();
            JavaPlugin javaPlugin = plugin.getPlugin();
            this.landsIntegration = LandsIntegration.of(javaPlugin);
        }

        return this.landsIntegration;
    }

    private @Nullable Area getArea(@NotNull Location location) {
        LandsIntegration lands = getLandsIntegration();
        return lands.getArea(location);
    }

    private @Nullable RoleFlag getRoleFlag(@NotNull TagType tagType, @Nullable Entity enemy) {
        switch (tagType) {
            case MOB:
            case PLAYER:
            case MYTHIC_MOB:
                return getRoleFlag(enemy);
            case DAMAGE:
                return Flags.NO_DAMAGE;
            default: break;
        }

        return null;
    }

    private @Nullable RoleFlag getRoleFlag(@Nullable Entity enemy) {
        if (enemy instanceof Player) {
            return Flags.ATTACK_PLAYER;
        }

        if (enemy instanceof Animals) {
            return Flags.ATTACK_ANIMAL;
        }

        if (enemy instanceof Monster) {
            return Flags.ATTACK_MONSTER;
        }

        return null;
    }
}

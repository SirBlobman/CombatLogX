package combatlogx.expansion.compatibility.region.lands;

import java.util.UUID;

import org.bukkit.Location;
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

public final class LandsRegionHandler extends RegionHandler {
    private final LandsIntegration landsIntegration;

    public LandsRegionHandler(LandsExpansion expansion) {
        super(expansion);
        ICombatLogX plugin = expansion.getPlugin();
        JavaPlugin javaPlugin = plugin.getPlugin();
        this.landsIntegration = LandsIntegration.of(javaPlugin);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.lands.no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagInformation tagInformation) {
        TagType tagType = tagInformation.getCurrentTagType();
        if (tagType == TagType.UNKNOWN) {
            return false;
        }

        LandsIntegration api = getLandsIntegration();
        Area area = api.getArea(location);
        if (area == null) {
            return false;
        }


        UUID playerId = player.getUniqueId();
        RoleFlag flag = (tagType == TagType.PLAYER ? Flags.ATTACK_PLAYER : Flags.ATTACK_ANIMAL);
        return !area.hasRoleFlag(playerId, flag);
    }

    private LandsIntegration getLandsIntegration() {
        return this.landsIntegration;
    }
}

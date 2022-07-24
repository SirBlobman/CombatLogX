package combatlogx.expansion.compatibility.region.lands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.flags.types.RoleFlag;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;

public final class LandsRegionHandler extends RegionHandler {
    private final LandsIntegration landsIntegration;

    public LandsRegionHandler(LandsExpansion expansion) {
        super(expansion);
        ICombatLogX plugin = expansion.getPlugin();
        JavaPlugin javaPlugin = plugin.getPlugin();
        this.landsIntegration = new LandsIntegration(javaPlugin);
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

        Area area = this.landsIntegration.getAreaByLoc(location);
        RoleFlag flag = (tagType == TagType.PLAYER ? Flags.ATTACK_PLAYER : Flags.ATTACK_ANIMAL);
        return (area != null && !area.hasFlag(player, flag, false));
    }
}

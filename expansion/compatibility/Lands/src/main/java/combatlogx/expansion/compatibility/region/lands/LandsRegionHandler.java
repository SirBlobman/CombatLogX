package combatlogx.expansion.compatibility.region.lands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.region.RegionHandler;
import com.SirBlobman.combatlogx.api.object.TagType;

import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.role.enums.RoleSetting;

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
        return "expansion.lands-compatibility-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        if(tagType == TagType.UNKNOWN) return false;
        Area area = this.landsIntegration.getAreaByLoc(location);

        RoleSetting roleSetting = (tagType == TagType.PLAYER ? RoleSetting.ATTACK_PLAYER : RoleSetting.ATTACK_ANIMAL);
        return (area != null && !area.canSetting(player, roleSetting, false));
    }
}
package combatlogx.expansion.compatibility.region.redprotect;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;
import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;

public class RedProtectRegionHandler extends RegionHandler {
    public RedProtectRegionHandler(RedProtectExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.redprotect-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagInformation tagInformation) {
        TagType tagType = tagInformation.getCurrentTagType();
        if (tagType != TagType.PLAYER) {
            return false;
        }

        RedProtect redProtect = RedProtect.get();
        RedProtectAPI redProtectApi = redProtect.getAPI();

        Region region = redProtectApi.getRegion(location);
        return (region != null && !region.getFlagBool("pvp"));
    }
}

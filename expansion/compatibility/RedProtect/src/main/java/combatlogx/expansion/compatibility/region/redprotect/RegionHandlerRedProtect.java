package combatlogx.expansion.compatibility.region.redprotect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;
import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;

public class RegionHandlerRedProtect extends RegionHandler<RedProtectExpansion> {
    public RegionHandlerRedProtect(@NotNull RedProtectExpansion expansion) {
        super(expansion);
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.redprotect-no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        TagType tagType = tag.getCurrentTagType();
        if (tagType != TagType.PLAYER) {
            return false;
        }

        Region region = getRegion(location);
        if (region == null) {
            return false;
        }

        return !region.getFlagBool("pvp");
    }

    private @NotNull RedProtect getRedProtection() {
        return RedProtect.get();
    }

    private @NotNull RedProtectAPI getAPI() {
        RedProtect redProtect = getRedProtection();
        return redProtect.getAPI();
    }

    private @Nullable Region getRegion(@NotNull Location location) {
        RedProtectAPI api = getAPI();
        return api.getRegion(location);
    }
}

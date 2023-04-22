package combatlogx.expansion.compatibility.region.preciousstones;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.api.IApi;
import net.sacredlabyrinth.Phaed.PreciousStones.field.FieldFlag;

public final class RegionHandlerPreciousStones extends RegionHandler<PreciousStonesExpansion> {
    public RegionHandlerPreciousStones(@NotNull PreciousStonesExpansion expansion) {
        super(expansion);
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        return "expansion.region-protection.preciousstones.no-entry";
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        IApi api = getAPI();
        TagType tagType = tag.getCurrentTagType();
        FieldFlag fieldFlag = getFieldFlag(tagType);
        if (fieldFlag == null) {
            return false;
        }

        return api.isFieldProtectingArea(fieldFlag, location);
    }

    private @NotNull IApi getAPI() {
        return PreciousStones.API();
    }

    private @Nullable FieldFlag getFieldFlag(@NotNull TagType tagType) {
        switch (tagType) {
            case PLAYER:
                return FieldFlag.PREVENT_PVP;
            case MOB:
            case MYTHIC_MOB:
                return FieldFlag.PREVENT_MOB_DAMAGE;
            default:
                break;
        }

        return null;
    }
}

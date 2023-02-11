package combatlogx.expansion.compatibility.region.husktowns;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import net.william278.husktowns.api.HuskTownsAPI;
import net.william278.husktowns.claim.Position;
import net.william278.husktowns.listener.Operation;
import net.william278.husktowns.listener.Operation.Type;
import net.william278.husktowns.user.OnlineUser;

public final class HuskTownsRegionHandler extends RegionHandler {
    public HuskTownsRegionHandler(HuskTownsExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.husktowns-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagInformation tagInformation) {
        TagType tagType = tagInformation.getCurrentTagType();
        HuskTownsAPI api = HuskTownsAPI.getInstance();

        OnlineUser user = api.getOnlineUser(player);
        if (user == null) {
            return false;
        }

        Position position = api.getPosition(location);
        if (position == null) {
            return false;
        }

        if (tagType == TagType.PLAYER) {
            Operation operation = Operation.of(user, Type.PLAYER_DAMAGE_PLAYER, position, true);
            return !api.isOperationAllowed(operation);
        }

        if (tagType == TagType.MOB) {
            Operation operation = Operation.of(user, Type.PLAYER_DAMAGE_ENTITY, position, true);
            return !api.isOperationAllowed(operation);
        }

        return false;
    }
}

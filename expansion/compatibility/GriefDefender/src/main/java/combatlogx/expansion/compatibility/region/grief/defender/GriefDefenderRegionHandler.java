package combatlogx.expansion.compatibility.region.grief.defender;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.google.common.reflect.TypeToken;
import com.griefdefender.api.Core;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.Tristate;
import com.griefdefender.api.User;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimManager;
import com.griefdefender.api.permission.Context;
import com.griefdefender.api.permission.option.Options;

public final class GriefDefenderRegionHandler extends RegionHandler {
    public GriefDefenderRegionHandler(GriefDefenderExpansion expansion) {
        super(expansion);
    }
    
    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.griefdefender-no-entry";
    }
    
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        if(tagType != TagType.PLAYER) return false;
        
        Claim claim = getClaimAt(location);
        if(claim == null) return false;
        
        UUID uuid = player.getUniqueId();
        Core core = GriefDefender.getCore();
        User user = core.getUser(uuid);
        
        Set<Context> contexts = Collections.emptySet();
        TypeToken<Tristate> typeTokenTristate = TypeToken.of(Tristate.class);
        Tristate activeOptionValue = claim.getActiveOptionValue(typeTokenTristate, Options.PVP, user, contexts);
        
        return (activeOptionValue != Tristate.TRUE);
    }
    
    private Claim getClaimAt(Location location) {
        World world = location.getWorld();
        if(world == null) return null;
        
        UUID worldId = world.getUID();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
        Core core = GriefDefender.getCore();
        ClaimManager claimManager = core.getClaimManager(worldId);
        return claimManager.getClaimAt(x, y, z);
    }
}

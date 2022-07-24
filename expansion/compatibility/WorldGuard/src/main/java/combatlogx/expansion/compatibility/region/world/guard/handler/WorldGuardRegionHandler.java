package combatlogx.expansion.compatibility.region.world.guard.handler;

import java.util.Locale;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

import combatlogx.expansion.compatibility.region.world.guard.WorldGuardExpansion;
import combatlogx.expansion.compatibility.region.world.guard.hook.HookWorldGuard;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;

public final class WorldGuardRegionHandler extends RegionHandler {
    private final WorldGuardExpansion expansion;

    public WorldGuardRegionHandler(WorldGuardExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    private WorldGuardExpansion getWorldGuardExpansion() {
        return this.expansion;
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        String tagTypeName = tagType.name();
        String tagTypeLower = tagTypeName.toLowerCase(Locale.US);
        return ("expansion.region-protection.worldguard.no-entry-" + tagTypeLower + "-combat");
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagInformation tagInformation) {
        TagType tagType = tagInformation.getCurrentTagType();
        WorldGuardWrapper wrappedWorldGuard = WorldGuardWrapper.getInstance();
        IWrappedFlag<WrappedState> wrappedFlag = getFlag(tagType);
        if (wrappedFlag == null) {
            return false;
        }

        Optional<WrappedState> optionalWrappedState = wrappedWorldGuard.queryFlag(player, location, wrappedFlag);
        if (optionalWrappedState.isPresent()) {
            WrappedState wrappedState = optionalWrappedState.get();
            return (wrappedState == WrappedState.DENY);
        }

        return false;
    }

    private IWrappedFlag<WrappedState> getFlag(TagType tagType) {
        WorldGuardExpansion expansion = getWorldGuardExpansion();
        HookWorldGuard hook = expansion.getHookWorldGuard();

        switch (tagType) {
            case PLAYER:
                return hook.getPlayerCombatFlag();
            case MOB:
                return hook.getMobCombatFlag();
            case MYTHIC_MOB:
                return hook.getMythicMobCombatFlag();
            case DAMAGE:
                return hook.getDamageCombatFlag();
            case UNKNOWN:
                return hook.getUnknownCombatFlag();
            default:
                break;
        }

        return null;
    }
}

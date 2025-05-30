package combatlogx.expansion.compatibility.region.world.guard.handler;

import java.util.Locale;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

import combatlogx.expansion.compatibility.region.world.guard.WorldGuardConfiguration;
import combatlogx.expansion.compatibility.region.world.guard.WorldGuardExpansion;
import combatlogx.expansion.compatibility.region.world.guard.hook.HookWorldGuard;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;

public final class WorldGuardRegionHandler extends RegionHandler<WorldGuardExpansion> {
    public WorldGuardRegionHandler(@NotNull WorldGuardExpansion expansion) {
        super(expansion);
    }

    @Override
    public @NotNull String getEntryDeniedMessagePath(@NotNull TagType tagType) {
        String tagTypeName = tagType.name();
        String tagTypeLower = tagTypeName.toLowerCase(Locale.US);
        String nameFormat = "expansion.region-protection.worldguard.no-entry-%s-combat";
        return String.format(Locale.US, nameFormat, tagTypeLower);
    }

    @Override
    public boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        TagType tagType = tag.getCurrentTagType();
        WorldGuardWrapper wrappedWorldGuard = WorldGuardWrapper.getInstance();
        IWrappedFlag<WrappedState> wrappedFlag = getFlag(tagType);
        if (wrappedFlag == null) {
            return false;
        }

        // First check for custom CombatLogX flag.
        Optional<WrappedState> optionalWrappedState = wrappedWorldGuard.queryFlag(player, location, wrappedFlag);
        if (optionalWrappedState.isPresent()) {
            WrappedState wrappedState = optionalWrappedState.get();
            return (wrappedState == WrappedState.DENY);
        }

        WorldGuardExpansion expansion = getExpansion();
        WorldGuardConfiguration configuration = expansion.getWorldGuardConfiguration();
        if (!configuration.isUsePvpFlag()) {
            // 'use-pvp-flag' option is disabled in configuration.
            return false;
        }

        WrappedState pvpState = getPvpState(player, location);
        return (pvpState == WrappedState.DENY);
    }

    /**
     * @param player The player being checked (might bypass or ignore certain flags)
     * @param location The coordinates to check.
     * @return {@code null} if the 'pvp' flag doesn't exist. {@code null} if the location doesn't have the flag.
     * Otherwise, a {@link WrappedState} depending on the region flags of the location.
     */
    private @Nullable WrappedState getPvpState(@NotNull Player player, @NotNull Location location) {
        WorldGuardWrapper wrappedWorldGuard = WorldGuardWrapper.getInstance();
        Optional<IWrappedFlag<WrappedState>> optionalPvpFlag = wrappedWorldGuard.getFlag("pvp", WrappedState.class);
        if (!optionalPvpFlag.isPresent()) {
            // 'pvp' flag does not exist in the WorldGuard plugin (nearly impossible).
            return null;
        }

        IWrappedFlag<WrappedState> pvpFlag = optionalPvpFlag.get();
        Optional<WrappedState> optionalPvpState = wrappedWorldGuard.queryFlag(player, location, pvpFlag);
        return optionalPvpState.orElse(null); // Missing 'pvp' flag state from location will be ignored.

    }

    private IWrappedFlag<WrappedState> getFlag(TagType tagType) {
        WorldGuardExpansion expansion = getExpansion();
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

    @Override
    protected void customPreventEntry(@NotNull Cancellable e, @NotNull Player player,
                                      @NotNull TagInformation tagInformation, @NotNull Location fromLocation,
                                      @NotNull Location toLocation) {
        WorldGuardExpansion expansion = getExpansion();
        HookWorldGuard hook = expansion.getHookWorldGuard();
        IWrappedFlag<Boolean> retagFlag = hook.getRetagFlag();

        WorldGuardWrapper wrappedWorldGuard = WorldGuardWrapper.getInstance();
        Optional<Boolean> optionalFlag = wrappedWorldGuard.queryFlag(player, toLocation, retagFlag);
        if (!optionalFlag.isPresent() || !optionalFlag.get()) {
            return;
        }

        ICombatLogX combatLogX = getExpansion().getPlugin();
        ICombatManager combatManager = combatLogX.getCombatManager();
        Entity currentEnemy = tagInformation.getCurrentEnemy();
        TagType currentTagType = tagInformation.getCurrentTagType();
        combatManager.tag(player, currentEnemy, currentTagType, TagReason.UNKNOWN);
    }
}

package combatlogx.expansion.cheat.prevention.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnums;

public final class TeleportConfiguration implements ITeleportConfiguration {
    private boolean preventPortals;
    private boolean preventTeleportation;
    private boolean enderPearlRetag;
    private boolean untag;

    private final Set<TeleportCause> allowedTeleportCauseSet;

    public TeleportConfiguration() {
        this.preventPortals = true;
        this.preventTeleportation = true;
        this.enderPearlRetag = false;
        this.untag = false;
        this.allowedTeleportCauseSet = EnumSet.noneOf(TeleportCause.class);
    }

    @Override
    public void load(ConfigurationSection config) {
        setPreventPortals(config.getBoolean("prevent-portals", true));
        setPreventTeleportation(config.getBoolean("prevent-teleportation", true));
        setEnderPearlRetag(config.getBoolean("ender-pearl-retag", false));
        setUntag(config.getBoolean("untag", false));

        List<String> allowedTeleportCauseNameList = config.getStringList("allowed-teleport-cause-list");
        setAllowedTeleportCauses(parseEnums(allowedTeleportCauseNameList, TeleportCause.class));
    }

    @Override
    public boolean isPreventPortals() {
        return this.preventPortals;
    }

    public void setPreventPortals(boolean preventPortals) {
        this.preventPortals = preventPortals;
    }

    @Override
    public boolean isPreventTeleportation() {
        return this.preventTeleportation;
    }

    public void setPreventTeleportation(boolean preventTeleportation) {
        this.preventTeleportation = preventTeleportation;
    }

    @Override
    public boolean isEnderPearlRetag() {
        return this.enderPearlRetag;
    }

    public void setEnderPearlRetag(boolean enderPearlRetag) {
        this.enderPearlRetag = enderPearlRetag;
    }

    @Override
    public boolean isUntag() {
        return this.untag;
    }

    public void setUntag(boolean untag) {
        this.untag = untag;
    }

    public @NotNull Set<TeleportCause> getAllowedTeleportCauses() {
        return Collections.unmodifiableSet(this.allowedTeleportCauseSet);
    }

    public void setAllowedTeleportCauses(@NotNull Collection<TeleportCause> causes) {
        this.allowedTeleportCauseSet.clear();
        this.allowedTeleportCauseSet.addAll(causes);
    }

    @Override
    public boolean isAllowed(@NotNull TeleportCause cause) {
        Set<TeleportCause> causeSet = getAllowedTeleportCauses();
        return causeSet.contains(cause);
    }
}

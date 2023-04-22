package combatlogx.expansion.compatibility.mythicmobs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class MythicMobsConfiguration implements IConfigurable {
    private final Set<String> noTagTypeSet;
    private final Set<String> forceTagTypeSet;

    public MythicMobsConfiguration() {
        this.noTagTypeSet = new HashSet<>();
        this.forceTagTypeSet = new HashSet<>();
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setNoTagTypes(config.getStringList("no-tag-mob-type-list"));
        setForceTagTypes(config.getStringList("force-tag-mob-type-list"));
    }

    public @NotNull Set<String> getNoTagTypes() {
        return Collections.unmodifiableSet(this.noTagTypeSet);
    }

    public void setNoTagTypes(@NotNull Collection<String> types) {
        this.noTagTypeSet.clear();
        this.noTagTypeSet.addAll(types);
    }

    public @NotNull Set<String> getForceTagTypes() {
        return Collections.unmodifiableSet(this.forceTagTypeSet);
    }

    public void setForceTagTypes(@NotNull Collection<String> types) {
        this.forceTagTypeSet.clear();
        this.forceTagTypeSet.addAll(types);
    }

    public boolean isForceTag(@NotNull String mobName) {
        Set<String> typeSet = getForceTagTypes();
        return typeSet.contains(mobName);
    }

    public boolean isNoTag(@NotNull String mobName) {
        Set<String> typeSet = getNoTagTypes();
        return typeSet.contains(mobName);
    }
}

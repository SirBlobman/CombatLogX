package combatlogx.expansion.newbie.helper.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class WorldsConfiguration implements IConfigurable {
    private final Set<String> forcedPvpWorldNameSet;
    private final Set<String> noPvpWorldNameSet;

    public WorldsConfiguration() {
        this.forcedPvpWorldNameSet = new HashSet<>();
        this.noPvpWorldNameSet = new HashSet<>();
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setForcedPvpWorlds(section.getStringList("forced-pvp-world-list"));
        setNoPvpWorlds(section.getStringList("no-pvp-world-list"));
    }

    public @NotNull Set<String> getForcedPvpWorlds() {
        return Collections.unmodifiableSet(this.forcedPvpWorldNameSet);
    }

    public void setForcedPvpWorlds(@NotNull Collection<String> worlds) {
        this.forcedPvpWorldNameSet.clear();
        this.forcedPvpWorldNameSet.addAll(worlds);
    }

    public @NotNull Set<String> getNoPvpWorlds() {
        return Collections.unmodifiableSet(this.noPvpWorldNameSet);
    }

    public void setNoPvpWorlds(@NotNull Collection<String> worlds) {
        this.noPvpWorldNameSet.clear();
        this.noPvpWorldNameSet.addAll(worlds);
    }

    public boolean isForcePvp(@NotNull World world) {
        String worldName = world.getName();
        Set<String> worldNameSet = getForcedPvpWorlds();
        return worldNameSet.contains(worldName);
    }

    public boolean isNoPvp(@NotNull World world) {
        String worldName = world.getName();
        Set<String> worldNameSet = getForcedPvpWorlds();
        return worldNameSet.contains(worldName);
    }
}

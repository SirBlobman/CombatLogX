package com.github.sirblobman.combatlogx.api.expansion.region.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.combatlogx.api.object.NoEntryMode;

import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnum;
import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnums;

public final class RegionExpansionConfiguration implements IConfigurable {
    private NoEntryMode noEntryMode;
    private double knockbackStrength;
    private int messageCooldown;
    private boolean preventTeleport;
    private Set<TeleportCause> ignoredTeleportCauseSet;

    public RegionExpansionConfiguration() {
        this.noEntryMode = NoEntryMode.KNOCKBACK_PLAYER;
        this.knockbackStrength = 1.5D;
        this.messageCooldown = 30;
        this.preventTeleport = true;
        this.ignoredTeleportCauseSet = EnumSet.noneOf(TeleportCause.class);
    }

    @Override
    public void load(ConfigurationSection config) {
        setKnockbackStrength(config.getDouble("knockback-strength", 1.5D));
        setMessageCooldown(config.getInt("message-cooldown", 30));
        setPreventTeleport(config.getBoolean("prevent-teleport", true));

        String noEntryModeName = config.getString("no-entry-mode", "KNOCKBACK_PLAYER");
        setNoEntryMode(parseEnum(NoEntryMode.class, noEntryModeName, NoEntryMode.KNOCKBACK_PLAYER));

        List<String> ignoredTeleportCauseNameList = config.getStringList("ignored-teleport-cause-list");
        setIgnoredTeleportCauses(parseEnums(ignoredTeleportCauseNameList, TeleportCause.class));
    }

    public boolean isPreventTeleport() {
        return this.preventTeleport;
    }

    public void setPreventTeleport(boolean preventTeleport) {
        this.preventTeleport = preventTeleport;
    }

    public @NotNull NoEntryMode getNoEntryMode() {
        return this.noEntryMode;
    }

    public void setNoEntryMode(@NotNull NoEntryMode noEntryMode) {
        this.noEntryMode = noEntryMode;
    }

    public double getKnockbackStrength() {
        return this.knockbackStrength;
    }

    public void setKnockbackStrength(double knockbackStrength) {
        this.knockbackStrength = knockbackStrength;
    }

    public int getMessageCooldown() {
        return this.messageCooldown;
    }

    public void setMessageCooldown(int messageCooldown) {
        this.messageCooldown = messageCooldown;
    }

    public @NotNull Set<TeleportCause> getIgnoredTeleportCauses() {
        return Collections.unmodifiableSet(this.ignoredTeleportCauseSet);
    }

    public void setIgnoredTeleportCauses(@NotNull Collection<TeleportCause> causes) {
        this.ignoredTeleportCauseSet.clear();
        this.ignoredTeleportCauseSet.addAll(causes);
    }

    public boolean isIgnored(@NotNull TeleportCause cause) {
        Set<TeleportCause> causes = getIgnoredTeleportCauses();
        return causes.contains(cause);
    }
}

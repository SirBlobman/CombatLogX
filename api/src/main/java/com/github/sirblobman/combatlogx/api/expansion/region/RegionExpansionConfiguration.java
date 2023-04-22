package com.github.sirblobman.combatlogx.api.expansion.region;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.combatlogx.api.object.NoEntryMode;

import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnum;

public final class RegionExpansionConfiguration implements IConfigurable {
    private NoEntryMode noEntryMode;
    private double knockbackStrength;
    private int messageCooldown;

    public RegionExpansionConfiguration() {
        this.noEntryMode = NoEntryMode.KNOCKBACK_PLAYER;
        this.knockbackStrength = 1.5D;
        this.messageCooldown = 30;
    }

    @Override
    public void load(ConfigurationSection config) {
        setKnockbackStrength(config.getDouble("knockback-strength", 1.5D));
        setMessageCooldown(config.getInt("message-cooldown", 30));

        String noEntryModeName = config.getString("no-entry-mode", "KNOCKBACK_PLAYER");
        setNoEntryMode(parseEnum(NoEntryMode.class, noEntryModeName, NoEntryMode.KNOCKBACK_PLAYER));
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
}

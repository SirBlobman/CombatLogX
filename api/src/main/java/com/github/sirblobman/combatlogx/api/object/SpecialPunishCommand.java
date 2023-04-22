package com.github.sirblobman.combatlogx.api.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.utility.Validate;

import org.jetbrains.annotations.NotNull;

public final class SpecialPunishCommand implements IConfigurable {
    private final String id;

    private int amountMin;
    private int amountMax;
    private boolean reset;

    private final List<String> commandList;

    public SpecialPunishCommand(@NotNull String id) {
        this.id = Validate.notEmpty(id, "id must not be empty!");

        this.amountMin = 0;
        this.amountMax = 0;
        this.reset = false;
        this.commandList = new ArrayList<>();
    }

    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public void load(ConfigurationSection section) {
        ConfigurationSection amountSection = getOrCreateSection(section, "amount");
        setAmountMin(amountSection.getInt("min", 0));
        setAmountMax(amountSection.getInt("max", 0));

        setReset(section.getBoolean("reset", false));
        setCommands(section.getStringList("command-list"));
    }

    public int getAmountMin() {
        return amountMin;
    }

    public void setAmountMin(int amountMin) {
        this.amountMin = amountMin;
    }

    public int getAmountMax() {
        return amountMax;
    }

    public void setAmountMax(int amountMax) {
        this.amountMax = amountMax;
    }

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    public @NotNull List<String> getCommands() {
        return Collections.unmodifiableList(this.commandList);
    }

    public void setCommands(@NotNull Collection<String> commands) {
        this.commandList.clear();
        this.commandList.addAll(commands);
    }
}

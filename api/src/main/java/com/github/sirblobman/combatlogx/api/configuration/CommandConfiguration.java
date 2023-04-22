package com.github.sirblobman.combatlogx.api.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.combatlogx.api.object.SpecialPunishCommand;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandConfiguration implements IConfigurable {
    private final List<String> tagCommandList;
    private final List<String> untagCommandList;
    private final List<String> punishCommandList;

    private boolean specialPunishCommandsEnabled;
    private final Map<String, SpecialPunishCommand> specialPunishCommandMap;

    public CommandConfiguration() {
        this.tagCommandList = new ArrayList<>();
        this.untagCommandList = new ArrayList<>();
        this.punishCommandList = new ArrayList<>();

        this.specialPunishCommandsEnabled = false;
        this.specialPunishCommandMap = new HashMap<>();
    }

    @Override
    public void load(ConfigurationSection config) {
        setTagCommands(config.getStringList("tag-command-list"));
        setUntagCommands(config.getStringList("untag-command-list"));
        setPunishCommands(config.getStringList("punish-command-list"));
        setSpecialPunishCommandsEnabled(config.getBoolean("special-punish-commands-enabled"));

        removeSpecialPunishCommands();
        ConfigurationSection sectionSpecials = getOrCreateSection(config, "special-punish-commands");
        Set<String> specialIdSet = sectionSpecials.getKeys(false);

        for (String specialId : specialIdSet) {
            ConfigurationSection section = sectionSpecials.getConfigurationSection(specialId);
            if (section == null) {
                continue;
            }

            SpecialPunishCommand command = new SpecialPunishCommand(specialId);
            command.load(section);
            addSpecialPunishCommand(command);
        }
    }

    public @NotNull List<String> getTagCommands() {
        return Collections.unmodifiableList(this.tagCommandList);
    }

    public void setTagCommands(@NotNull Collection<String> commands) {
        this.tagCommandList.clear();
        this.tagCommandList.addAll(commands);
    }

    public @NotNull List<String> getUntagCommands() {
        return Collections.unmodifiableList(this.untagCommandList);
    }

    public void setUntagCommands(@NotNull Collection<String> commands) {
        this.untagCommandList.clear();
        this.untagCommandList.addAll(commands);
    }

    public @NotNull List<String> getPunishCommands() {
        return Collections.unmodifiableList(this.punishCommandList);
    }

    public void setPunishCommands(@NotNull Collection<String> commands) {
        this.punishCommandList.clear();
        this.punishCommandList.addAll(commands);
    }

    public @NotNull Map<String, SpecialPunishCommand> getSpecialPunishCommands() {
        return Collections.unmodifiableMap(this.specialPunishCommandMap);
    }

    public @NotNull List<SpecialPunishCommand> getSpecialPunishCommandList() {
        Collection<SpecialPunishCommand> valueCollection = this.specialPunishCommandMap.values();
        return Collections.unmodifiableList(new ArrayList<>(valueCollection));
    }

    public @Nullable SpecialPunishCommand getSpecialPunishCommand(String id) {
        return this.specialPunishCommandMap.get(id);
    }

    public void addSpecialPunishCommand(SpecialPunishCommand command) {
        String id = command.getId();
        this.specialPunishCommandMap.put(id, command);
    }

    public void removeSpecialPunishCommands() {
        this.specialPunishCommandMap.clear();
    }

    public boolean isSpecialPunishCommandsEnabled() {
        return specialPunishCommandsEnabled;
    }

    public void setSpecialPunishCommandsEnabled(boolean specialPunishCommandsEnabled) {
        this.specialPunishCommandsEnabled = specialPunishCommandsEnabled;
    }
}

package com.github.sirblobman.combatlogx.api.expansion;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExpansionDescription {
    private final String mainClass;
    private final String name;
    private final String version;
    private final String prefix;
    private final String description;
    private final String website;

    private final List<String> authorList;
    private final List<String> pluginDependList;
    private final List<String> pluginSoftDependList;
    private final List<String> expansionDependList;
    private final List<String> expansionSoftDependList;

    private final boolean lateLoad;

    ExpansionDescription(@NotNull String mainClass, @NotNull String name, @NotNull String version,
                         @NotNull String prefix, @NotNull String description, @Nullable String website,
                         @NotNull List<String> authorList, @NotNull List<String> pluginDependList,
                         @NotNull List<String> pluginSoftDependList, @NotNull List<String> expansionDependList,
                         @NotNull List<String> expansionSoftDependList, boolean lateLoad) {
        this.mainClass = mainClass;
        this.name = name;
        this.version = version;
        this.prefix = prefix;

        this.description = description;
        this.website = website;

        this.authorList = authorList;
        this.pluginDependList = pluginDependList;
        this.pluginSoftDependList = pluginSoftDependList;
        this.expansionDependList = expansionDependList;
        this.expansionSoftDependList = expansionSoftDependList;

        this.lateLoad = lateLoad;
    }

    public @NotNull String getMainClass() {
        return this.mainClass;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull String getPrefix() {
        return this.prefix;
    }

    public @NotNull String getVersion() {
        return this.version;
    }

    public @NotNull String getDescription() {
        return this.description;
    }

    public @Nullable String getWebsite() {
        return this.website;
    }

    public @NotNull List<String> getAuthors() {
        return Collections.unmodifiableList(this.authorList);
    }

    public @NotNull List<String> getPluginDependencies() {
        return Collections.unmodifiableList(this.pluginDependList);
    }

    public @NotNull List<String> getPluginSoftDependencies() {
        return Collections.unmodifiableList(this.pluginSoftDependList);
    }

    public @NotNull List<String> getExpansionDependencies() {
        return Collections.unmodifiableList(this.expansionDependList);
    }

    public @NotNull List<String> getExpansionSoftDependencies() {
        return Collections.unmodifiableList(this.expansionSoftDependList);
    }

    public @NotNull String getFullName() {
        String displayName = getPrefix();
        String version = getVersion();
        return (displayName + " v" + version);
    }

    public boolean isLateLoad() {
        return this.lateLoad;
    }
}

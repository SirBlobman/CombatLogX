package com.github.sirblobman.combatlogx.api.expansion;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ExpansionDescriptionBuilder {
    private final String mainClass;
    private final String name;
    private final String version;

    private String prefix;
    private String description;
    private String website;

    private List<String> authorList;
    private List<String> pluginDependencyList;
    private List<String> expansionDependencyList;
    private List<String> pluginSoftDependencyList;
    private List<String> expansionSoftDependencyList;

    private boolean lateLoad;

    public ExpansionDescriptionBuilder(@NotNull String mainClass, @NotNull String name, @NotNull String version) {
        this.mainClass = mainClass;
        this.name = name;
        this.version = version;

        this.prefix = null;
        this.description = null;
        this.website = null;
        this.authorList = new ArrayList<>();
        this.pluginDependencyList = new ArrayList<>();
        this.expansionDependencyList = new ArrayList<>();
        this.pluginSoftDependencyList = new ArrayList<>();
        this.expansionSoftDependencyList = new ArrayList<>();
        this.lateLoad = false;
    }

    public @NotNull ExpansionDescriptionBuilder withPrefix(@Nullable String prefix) {
        this.prefix = prefix;
        return this;
    }

    public @NotNull ExpansionDescriptionBuilder withDescription(@Nullable String description) {
        this.description = description;
        return this;
    }

    public @NotNull ExpansionDescriptionBuilder withWebsite(@Nullable String website) {
        this.website = website;
        return this;
    }

    public @NotNull ExpansionDescriptionBuilder withAuthors(@NotNull List<String> authorList) {
        this.authorList = new ArrayList<>(authorList);
        return this;
    }

    public @NotNull ExpansionDescriptionBuilder withPluginDependencies(@NotNull List<String> list) {
        this.pluginDependencyList = new ArrayList<>(list);
        return this;
    }

    public @NotNull ExpansionDescriptionBuilder withPluginSoftDependencies(@NotNull List<String> list) {
        this.pluginSoftDependencyList = new ArrayList<>(list);
        return this;
    }

    public @NotNull ExpansionDescriptionBuilder withExpansionDependencies(@NotNull List<String> list) {
        this.expansionDependencyList = new ArrayList<>(list);
        return this;
    }

    public @NotNull ExpansionDescriptionBuilder withExpansionSoftDependencies(@NotNull List<String> list) {
        this.expansionSoftDependencyList = new ArrayList<>(list);
        return this;
    }

    public @NotNull ExpansionDescriptionBuilder withLateLoad(boolean lateLoad) {
        this.lateLoad = lateLoad;
        return this;
    }

    public @NotNull ExpansionDescription build() {
        String prefix = (this.prefix == null ? this.name : this.prefix);
        String description = (this.description == null ? "" : this.description);
        return new ExpansionDescription(this.mainClass, this.name, this.version, prefix, description, this.website,
                this.authorList, this.pluginDependencyList, this.pluginSoftDependencyList,
                this.expansionDependencyList, this.expansionSoftDependencyList, this.lateLoad);
    }
}

package com.github.sirblobman.combatlogx.api.expansion;

import java.util.ArrayList;
import java.util.List;

import com.github.sirblobman.api.utility.Validate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ExpansionDescriptionBuilder {
    private final String mainClassName, unlocalizedName, version;
    private String displayName, description, website;
    private List<String> authorList, pluginDependencyList, expansionDependencyList, pluginSoftDependencyList, expansionSoftDependencyList;
    private boolean lateLoad;

    public ExpansionDescriptionBuilder(String mainClassName, String unlocalizedName, String version) {
        this.mainClassName = Validate.notEmpty(mainClassName, "mainClassName must not be empty!");
        this.unlocalizedName = Validate.notEmpty(unlocalizedName, "unlocalizedName must not be empty!");
        this.version = Validate.notEmpty(version, "version must not be empty!");

        this.displayName = null;
        this.description = null;
        this.website = null;
        this.authorList = new ArrayList<>();
        this.pluginDependencyList = new ArrayList<>();
        this.expansionDependencyList = new ArrayList<>();
        this.pluginSoftDependencyList = new ArrayList<>();
        this.expansionSoftDependencyList = new ArrayList<>();
        this.lateLoad = false;
    }

    public ExpansionDescriptionBuilder withDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ExpansionDescriptionBuilder withDescription(@Nullable String description) {
        this.description = description;
        return this;
    }

    public ExpansionDescriptionBuilder withWebsite(@Nullable String website) {
        this.website = website;
        return this;
    }

    public ExpansionDescriptionBuilder withAuthors(List<String> authorList) {
        this.authorList = new ArrayList<>(authorList);
        return this;
    }

    public ExpansionDescriptionBuilder withPluginDependencies(List<String> pluginDependencyList) {
        this.pluginDependencyList = new ArrayList<>(pluginDependencyList);
        return this;
    }

    public ExpansionDescriptionBuilder withPluginSoftDependencies(List<String> pluginSoftDependencyList) {
        this.pluginSoftDependencyList = new ArrayList<>(pluginSoftDependencyList);
        return this;
    }

    public ExpansionDescriptionBuilder withExpansionDependencies(List<String> expansionDependencyList) {
        this.expansionDependencyList = new ArrayList<>(expansionDependencyList);
        return this;
    }

    public ExpansionDescriptionBuilder withExpansionSoftDependencies(List<String> expansionSoftDependencyList) {
        this.expansionSoftDependencyList = new ArrayList<>(expansionSoftDependencyList);
        return this;
    }

    public ExpansionDescriptionBuilder withLateLoad(boolean lateLoad) {
        this.lateLoad = lateLoad;
        return this;
    }

    @NotNull
    public ExpansionDescription build() {
        String displayName = (this.displayName == null ? this.unlocalizedName : this.displayName);
        String description = (this.description == null ? "" : this.description);
        return new ExpansionDescription(this.mainClassName, this.unlocalizedName, this.version, displayName,
                description, this.website, this.authorList, this.pluginDependencyList, this.pluginSoftDependencyList,
                this.expansionDependencyList, this.expansionSoftDependencyList, this.lateLoad);
    }
}

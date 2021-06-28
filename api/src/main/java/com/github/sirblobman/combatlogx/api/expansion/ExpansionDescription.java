package com.github.sirblobman.combatlogx.api.expansion;

import java.util.Collections;
import java.util.List;

import com.github.sirblobman.api.utility.Validate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExpansionDescription {
    private final String mainClassName, unlocalizedName, version, displayName, description, website;
    private final List<String> authorList, pluginDependList, pluginSoftDependList, expansionDependList,
            expansionSoftDependList;
    private final boolean lateLoad;
    ExpansionDescription(String mainClassName, String unlocalizedName, String version, String displayName,
                         String description, String website, List<String> authorList, List<String> pluginDependList,
                         List<String> pluginSoftDependList, List<String> expansionDependList,
                         List<String> expansionSoftDependList, boolean lateLoad) {
        this.mainClassName = Validate.notEmpty(mainClassName, "mainClassName cannot be empty or null!");
        this.unlocalizedName = Validate.notEmpty(unlocalizedName, "unlocalizedName cannot be empty or null!");
        this.version = Validate.notEmpty(version, "version cannot be empty or null!");

        this.displayName = displayName;
        this.description = description;
        this.website = website;

        this.authorList = authorList;
        this.pluginDependList = pluginDependList;
        this.pluginSoftDependList = pluginSoftDependList;
        this.expansionDependList = expansionDependList;
        this.expansionSoftDependList = expansionSoftDependList;

        this.lateLoad = lateLoad;
    }

    @NotNull
    public String getMainClassName() {
        return this.mainClassName;
    }

    @NotNull
    public String getUnlocalizedName() {
        return this.unlocalizedName;
    }

    @NotNull
    public String getDisplayName() {
        return this.displayName;
    }

    @NotNull
    public String getVersion() {
        return this.version;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    @Nullable
    public String getWebsite() {
        return this.website;
    }

    @NotNull
    public List<String> getAuthors() {
        return this.authorList;
    }

    @NotNull
    public List<String> getPluginDependencies() {
        return Collections.unmodifiableList(this.pluginDependList);
    }

    @NotNull
    public List<String> getPluginSoftDependencies() {
        return Collections.unmodifiableList(this.pluginSoftDependList);
    }

    @NotNull
    public List<String> getExpansionDependencies() {
        return Collections.unmodifiableList(this.expansionDependList);
    }

    @NotNull
    public List<String> getExpansionSoftDependencies() {
        return Collections.unmodifiableList(this.expansionSoftDependList);
    }

    @NotNull
    public String getFullName() {
        String displayName = getDisplayName();
        String version = getVersion();
        return (displayName + " v" + version);
    }

    public boolean isLateLoad() {
        return this.lateLoad;
    }
}

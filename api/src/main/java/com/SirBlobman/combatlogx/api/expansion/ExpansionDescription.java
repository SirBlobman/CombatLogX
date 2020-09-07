package com.SirBlobman.combatlogx.api.expansion;

import java.util.ArrayList;
import java.util.List;

import com.SirBlobman.api.utility.Validate;

public class ExpansionDescription {
    private final String mainClassName, unlocalizedName, displayName, description, version;
    private final List<String> authorList;
    public ExpansionDescription(String mainClassName, String unlocalizedName, String displayName, String description, String version, List<String> authorList) {
        this.mainClassName = Validate.notEmpty(mainClassName, "mainClassName cannot be empty or null!");
        this.unlocalizedName = Validate.notEmpty(unlocalizedName, "unlocalizedName cannot be empty or null!");
        this.version = Validate.notEmpty(version, "version cannot be empty or null!");
        this.displayName = (displayName == null ? unlocalizedName : displayName);
        this.description = (description == null ? "" : description);
        this.authorList = new ArrayList<>(authorList);
    }

    public String getUnlocalizedName() {
        return this.unlocalizedName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription() {
        return this.description;
    }

    public String getMainClassName() {
        return this.mainClassName;
    }

    public String getVersion() {
        return this.version;
    }

    public List<String> getAuthors() {
        return this.authorList;
    }

    public String getFullName() {
        String displayName = getDisplayName();
        String version = getVersion();
        return (displayName + " v" + version);
    }
}
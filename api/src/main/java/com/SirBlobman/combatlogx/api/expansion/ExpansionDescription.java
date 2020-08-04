package com.SirBlobman.combatlogx.api.expansion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ExpansionDescription {
    private final String name, displayName, description, mainClass, version;
    private final List<String> authorList;
    
    public ExpansionDescription(Builder builder) {
        this.mainClass = builder.getMainClass();
        this.name = builder.getUnlocalizedName();
        this.displayName = builder.getDisplayName();
        this.description = builder.getDescription();
        this.authorList = builder.getAuthors();
        this.version = builder.getVersion();
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getFullName() {
        String displayName = getDisplayName();
        String version = getVersion();
        return (displayName + " v" + version);
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public String getMainClass() {
        return this.mainClass;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public List<String> getAuthors() {
        return this.authorList;
    }
    
    public static class Builder {
        private final String unlocalizedName, mainClassName, version;
        private String displayName, description;
        private List<String> authorList;
        
        public Builder(String mainClassName, String unlocalizedName, String version) {
            this.mainClassName = Objects.requireNonNull(mainClassName, "mainClassName must not be null!");
            this.unlocalizedName = Objects.requireNonNull(unlocalizedName, "unlocalizedName must not be null!");
            this.version = Objects.requireNonNull(version, "version must not be null!");
        }
        
        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }
        
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }
        
        public Builder setAuthors(String... authorArray) {
            this.authorList = Arrays.asList(authorArray);
            return this;
        }
        
        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }
        
        public String getDisplayName() {
            return (this.displayName != null ? this.displayName : getUnlocalizedName());
        }
        
        public String getDescription() {
            return this.description;
        }
        
        public String getMainClass() {
            return this.mainClassName;
        }
        
        public String getVersion() {
            return this.version;
        }
        
        public List<String> getAuthors() {
            return new ArrayList<>(this.authorList);
        }
    }
}
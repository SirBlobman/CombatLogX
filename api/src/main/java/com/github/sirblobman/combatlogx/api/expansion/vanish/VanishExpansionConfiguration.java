package com.github.sirblobman.combatlogx.api.expansion.vanish;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class VanishExpansionConfiguration implements IConfigurable {
    private boolean preventVanishTaggingSelf;
    private boolean preventVanishTaggingOther;

    public VanishExpansionConfiguration() {
        this.preventVanishTaggingSelf = true;
        this.preventVanishTaggingOther = true;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setPreventVanishTaggingSelf(section.getBoolean("prevent-vanish-tagging-self", true));
        setPreventVanishTaggingOther(section.getBoolean("prevent-vanish-tagging-other", true));
    }

    public boolean isPreventVanishTaggingSelf() {
        return this.preventVanishTaggingSelf;
    }

    public void setPreventVanishTaggingSelf(boolean preventVanishTaggingSelf) {
        this.preventVanishTaggingSelf = preventVanishTaggingSelf;
    }

    public boolean isPreventVanishTaggingOther() {
        return this.preventVanishTaggingOther;
    }

    public void setPreventVanishTaggingOther(boolean preventVanishTaggingOther) {
        this.preventVanishTaggingOther = preventVanishTaggingOther;
    }
}

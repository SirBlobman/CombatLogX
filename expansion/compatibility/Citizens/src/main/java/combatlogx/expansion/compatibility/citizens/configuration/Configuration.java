package combatlogx.expansion.compatibility.citizens.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class Configuration implements IConfigurable {
    private boolean npcTagging;
    private boolean enableSentinel;

    public Configuration() {
        this.npcTagging = false;
        this.enableSentinel = true;
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setNpcTagging(config.getBoolean("npc-tagging", false));
        setEnableSentinel(config.getBoolean("enable-sentinel", true));
    }

    public boolean isNpcTagging() {
        return this.npcTagging;
    }

    public void setNpcTagging(boolean npcTagging) {
        this.npcTagging = npcTagging;
    }

    public boolean isEnableSentinel() {
        return this.enableSentinel;
    }

    public void setEnableSentinel(boolean enableSentinel) {
        this.enableSentinel = enableSentinel;
    }
}

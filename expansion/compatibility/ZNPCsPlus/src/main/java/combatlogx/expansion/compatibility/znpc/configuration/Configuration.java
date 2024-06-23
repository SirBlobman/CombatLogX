package combatlogx.expansion.compatibility.znpc.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class Configuration implements IConfigurable {
    private boolean npcTagging;

    public Configuration() {
        this.npcTagging = false;
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setNpcTagging(config.getBoolean("npc-tagging", false));
    }

    public boolean isNpcTagging() {
        return npcTagging;
    }

    public void setNpcTagging(boolean npcTagging) {
        this.npcTagging = npcTagging;
    }
}

package combatlogx.expansion.cheat.prevention.configuration;

import org.bukkit.configuration.ConfigurationSection;

public final class EntityConfiguration implements IEntityConfiguration {
    private boolean preventInteraction;

    public EntityConfiguration() {
        this.preventInteraction = false;
    }

    @Override
    public void load(ConfigurationSection config) {
        setPreventInteraction(config.getBoolean("prevent-interaction", false));
    }

    @Override
    public boolean isPreventInteraction() {
        return this.preventInteraction;
    }

    public void setPreventInteraction(boolean value) {
        this.preventInteraction = value;
    }
}

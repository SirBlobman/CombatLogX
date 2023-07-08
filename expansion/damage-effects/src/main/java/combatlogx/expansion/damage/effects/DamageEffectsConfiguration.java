package combatlogx.expansion.damage.effects;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

import combatlogx.expansion.damage.effects.effect.Blood;

public final class DamageEffectsConfiguration implements IConfigurable {
    private final Blood blood;
    private boolean allDamage;

    public DamageEffectsConfiguration() {
        this.allDamage = false;
        this.blood = new Blood();
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setAllDamage(config.getBoolean("all-damage", false));
        getBlood().load(getOrCreateSection(config, "blood"));
    }

    public boolean isAllDamage() {
        return this.allDamage;
    }

    public void setAllDamage(boolean allDamage) {
        this.allDamage = allDamage;
    }

    public @NotNull Blood getBlood() {
        return this.blood;
    }
}

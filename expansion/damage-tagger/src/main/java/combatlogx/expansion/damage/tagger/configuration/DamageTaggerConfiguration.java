package combatlogx.expansion.damage.tagger.configuration;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.sirblobman.api.configuration.IConfigurable;

import org.jetbrains.annotations.NotNull;

public final class DamageTaggerConfiguration implements IConfigurable {
    private boolean allDamage;
    private boolean endCrystals;
    private boolean retagOnly;

    private final Set<DamageCause> enabledDamageTypes;

    public DamageTaggerConfiguration() {
        this.allDamage = false;
        this.endCrystals = true;
        this.retagOnly = false;
        this.enabledDamageTypes = EnumSet.noneOf(DamageCause.class);
    }

    @Override
    public void load(ConfigurationSection config) {
        setAllDamage(config.getBoolean("all-damage", false));
        setEndCrystals(config.getBoolean("end-crystals", true));
        setRetagOnly(config.getBoolean("retag-only", false));

        ConfigurationSection sectionDamageType = getOrCreateSection(config, "damage-type");
        DamageCause[] damageCauses = DamageCause.values();

        disableAllDamageTypes();
        for (DamageCause damageCause : damageCauses) {
            String damageCauseName = damageCause.name().toLowerCase(Locale.US).replace('_', '-');
            if (sectionDamageType.getBoolean(damageCauseName, false)) {
                enableDamageType(damageCause);
            }
        }
    }

    public boolean isAllDamage() {
        return this.allDamage;
    }

    public void setAllDamage(boolean allDamage) {
        this.allDamage = allDamage;
    }

    public boolean isEndCrystals() {
        return this.endCrystals;
    }

    public void setEndCrystals(boolean endCrystals) {
        this.endCrystals = endCrystals;
    }

    public boolean isRetagOnly() {
        return this.retagOnly;
    }

    public void setRetagOnly(boolean retagOnly) {
        this.retagOnly = retagOnly;
    }

    public @NotNull Set<DamageCause> getEnabledDamageTypes() {
        return Collections.unmodifiableSet(this.enabledDamageTypes);
    }

    public void disableAllDamageTypes() {
        this.enabledDamageTypes.clear();
    }

    public void enableDamageType(@NotNull DamageCause cause) {
        this.enabledDamageTypes.add(cause);
    }

    public boolean isEnabled(@NotNull DamageCause cause) {
        Set<DamageCause> damageTypes = getEnabledDamageTypes();
        return damageTypes.contains(cause);
    }
}

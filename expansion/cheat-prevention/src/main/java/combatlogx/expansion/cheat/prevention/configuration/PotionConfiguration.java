package combatlogx.expansion.cheat.prevention.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

public final class PotionConfiguration implements IPotionConfiguration {
    private final Set<PotionEffectType> blockedPotionTypeSet;
    private boolean blockedPotionTypeSetInverted;

    public PotionConfiguration() {
        this.blockedPotionTypeSet = new HashSet<>();
        this.blockedPotionTypeSetInverted = false;
    }

    @Override
    public void load(ConfigurationSection config) {
        setBlockedPotionTypeSetInverted(config.getBoolean("blocked-potion-type-list-inverted", false));

        List<String> potionTypeNameList = config.getStringList("blocked-potion-type-list");
        List<PotionEffectType> potionEffectTypeList = new ArrayList<>();
        for (String potionTypeName : potionTypeNameList) {
            PotionEffectType potionEffectType = PotionEffectType.getByName(potionTypeName);
            if (potionEffectType != null) {
                potionEffectTypeList.add(potionEffectType);
            }
        }

        setBlockedPotionTypes(potionEffectTypeList);
    }

    public boolean isBlockedPotionTypeSetInverted() {
        return this.blockedPotionTypeSetInverted;
    }

    public void setBlockedPotionTypeSetInverted(boolean blockedPotionTypeSetInverted) {
        this.blockedPotionTypeSetInverted = blockedPotionTypeSetInverted;
    }

    public @NotNull Set<PotionEffectType> getBlockedPotionTypes() {
        return Collections.unmodifiableSet(this.blockedPotionTypeSet);
    }

    public void setBlockedPotionTypes(@NotNull Collection<PotionEffectType> types) {
        this.blockedPotionTypeSet.clear();
        this.blockedPotionTypeSet.addAll(types);
    }

    @Override
    public boolean isBlocked(@NotNull PotionEffectType effectType) {
        Set<PotionEffectType> blockedPotionTypeSet = getBlockedPotionTypes();
        boolean inverted = isBlockedPotionTypeSetInverted();
        boolean contains = blockedPotionTypeSet.contains(effectType);
        return (inverted != contains);
    }
}

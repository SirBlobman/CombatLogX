package combatlogx.expansion.cheat.prevention.configuration;

import org.bukkit.potion.PotionEffectType;

import com.github.sirblobman.api.configuration.IConfigurable;

import org.jetbrains.annotations.NotNull;

public interface IPotionConfiguration extends IConfigurable {
    boolean isBlocked(@NotNull PotionEffectType effectType);
}

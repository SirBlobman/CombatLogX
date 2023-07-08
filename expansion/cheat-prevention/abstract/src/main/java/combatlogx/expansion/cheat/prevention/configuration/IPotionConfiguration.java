package combatlogx.expansion.cheat.prevention.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.potion.PotionEffectType;

import com.github.sirblobman.api.configuration.IConfigurable;

public interface IPotionConfiguration extends IConfigurable {
    boolean isBlocked(@NotNull PotionEffectType effectType);
}

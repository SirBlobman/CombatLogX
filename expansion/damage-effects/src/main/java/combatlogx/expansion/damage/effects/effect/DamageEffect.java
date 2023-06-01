package combatlogx.expansion.damage.effects.effect;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

public interface DamageEffect {
    boolean isEnabled();
    void play(@NotNull Player player);
}

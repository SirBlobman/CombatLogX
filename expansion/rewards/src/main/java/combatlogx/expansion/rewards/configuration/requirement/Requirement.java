package combatlogx.expansion.rewards.configuration.requirement;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.IConfigurable;

import combatlogx.expansion.rewards.RewardExpansion;

public abstract class Requirement implements IConfigurable {
    private final RewardExpansion expansion;
    private final String id;

    private boolean checkEnemy;

    public Requirement(@NotNull RewardExpansion expansion, @NotNull String id) {
        this.expansion = expansion;
        this.id = id;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setCheckEnemy(section.getBoolean("check-enemy"));
    }

    protected final @NotNull RewardExpansion getExpansion() {
        return this.expansion;
    }

    public final @NotNull String getId() {
        return this.id;
    }

    public final boolean isCheckEnemy() {
        return this.checkEnemy;
    }

    public final void setCheckEnemy(boolean checkEnemy) {
        this.checkEnemy = checkEnemy;
    }

    public boolean meetsRequirement(LivingEntity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            return meetsRequirement(player);
        }

        return false;
    }

    public abstract boolean meetsRequirement(Player player);
}

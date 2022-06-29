package combatlogx.expansion.rewards.requirement;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.Validate;

import combatlogx.expansion.rewards.RewardExpansion;

public abstract class Requirement {
    private final RewardExpansion expansion;
    private final boolean enemy;

    public Requirement(RewardExpansion expansion, boolean enemy) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
        this.enemy = enemy;
    }

    public final RewardExpansion getExpansion() {
        return this.expansion;
    }

    public final boolean isEnemy() {
        return this.enemy;
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

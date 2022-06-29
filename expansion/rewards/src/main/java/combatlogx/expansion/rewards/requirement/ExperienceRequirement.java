package combatlogx.expansion.rewards.requirement;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.ExperienceUtility;

import combatlogx.expansion.rewards.RewardExpansion;

public final class ExperienceRequirement extends Requirement {
    private final int amount;

    public ExperienceRequirement(RewardExpansion expansion, boolean enemy, int amount) {
        super(expansion, enemy);
        this.amount = Math.max(0, amount);
    }

    public int getAmount() {
        return this.amount;
    }

    @Override
    public boolean meetsRequirement(Player player) {
        int amount = getAmount();
        int balance = ExperienceUtility.getExp(player);
        return (balance >= amount);
    }
}

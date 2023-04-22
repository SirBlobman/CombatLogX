package combatlogx.expansion.rewards.configuration.requirement;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.ExperienceUtility;

import combatlogx.expansion.rewards.RewardExpansion;

public final class ExperienceRequirement extends Requirement {
    private int amount;

    public ExperienceRequirement(@NotNull RewardExpansion expansion, @NotNull String id) {
        super(expansion, id);
        this.amount = 1;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        super.load(section);
        setAmount(section.getInt("amount", 1));
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean meetsRequirement(Player player) {
        int amount = getAmount();
        int balance = ExperienceUtility.getExp(player);
        return (balance >= amount);
    }
}

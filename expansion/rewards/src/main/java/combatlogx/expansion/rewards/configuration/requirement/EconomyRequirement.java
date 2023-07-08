package combatlogx.expansion.rewards.configuration.requirement;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import combatlogx.expansion.rewards.RewardExpansion;
import combatlogx.expansion.rewards.hook.HookVault;
import net.milkbowl.vault.economy.Economy;

public final class EconomyRequirement extends Requirement {
    private double amount;

    public EconomyRequirement(@NotNull RewardExpansion expansion, @NotNull String id) {
        super(expansion, id);
        this.amount = 1.0D;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean meetsRequirement(@NotNull Player player) {
        RewardExpansion expansion = getExpansion();
        HookVault vaultHook = expansion.getVaultHook();
        Economy economyHandler = vaultHook.getEconomyHandler();
        if (economyHandler == null) {
            return false;
        }

        double balance = economyHandler.getBalance(player);
        double amount = getAmount();
        return (balance >= amount);
    }
}

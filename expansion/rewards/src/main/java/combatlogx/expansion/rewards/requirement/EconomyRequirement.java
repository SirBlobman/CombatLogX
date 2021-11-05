package combatlogx.expansion.rewards.requirement;

import org.bukkit.entity.Player;

import combatlogx.expansion.rewards.RewardExpansion;
import combatlogx.expansion.rewards.hook.HookVault;
import net.milkbowl.vault.economy.Economy;

public final class EconomyRequirement extends Requirement {
    private final double amount;
    
    public EconomyRequirement(RewardExpansion expansion, boolean enemy, double amount) {
        super(expansion, enemy);
        this.amount = Math.max(0.0D, amount);
    }
    
    public double getAmount() {
        return this.amount;
    }
    
    @Override
    public boolean meetsRequirement(Player player) {
        RewardExpansion expansion = getExpansion();
        HookVault vaultHook = expansion.getVaultHook();
        Economy economyHandler = vaultHook.getEconomyHandler();
        
        double balance = economyHandler.getBalance(player);
        double amount = getAmount();
        return (balance >= amount);
    }
}

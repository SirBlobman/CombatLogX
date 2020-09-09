package combatlogx.expansion.rewards.hook;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import com.SirBlobman.api.utility.Validate;

import combatlogx.expansion.rewards.RewardExpansion;
import net.milkbowl.vault.economy.Economy;

public final class HookVault {
    private final RewardExpansion expansion;
    private Economy economy;
    public HookVault(RewardExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    public Economy getEconomyHandler() {
        return this.economy;
    }

    public boolean setupEconomy() {
        Logger logger = this.expansion.getLogger();
        try {
            ServicesManager servicesManager = Bukkit.getServicesManager();
            RegisteredServiceProvider<Economy> registration = servicesManager.getRegistration(Economy.class);
            if(registration == null) {
                logger.warning("An economy plugin is not registered.");
                return false;
            }

            Plugin plugin = registration.getPlugin();
            PluginDescriptionFile description = plugin.getDescription();
            String fullName = description.getFullName();

            this.economy = registration.getProvider();
            String economyName = this.economy.getName();

            logger.info("Successfully hooked into economy handler '" + economyName + "' from plugin '" + fullName + "'.");
            return true;
        } catch(Exception ex) {
            logger.log(Level.WARNING, "An error occurred while getting the economy handler:", ex);
            return false;
        }
    }
}
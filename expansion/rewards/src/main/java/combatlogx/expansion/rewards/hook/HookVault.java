package combatlogx.expansion.rewards.hook;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import combatlogx.expansion.rewards.RewardExpansion;
import net.milkbowl.vault.economy.Economy;

public final class HookVault {
    private final RewardExpansion expansion;
    private Economy economy;

    public HookVault(@NotNull RewardExpansion expansion) {
        this.expansion = expansion;
    }

    private @NotNull RewardExpansion getExpansion() {
        return this.expansion;
    }

    private @NotNull Logger getLogger() {
        RewardExpansion expansion = getExpansion();
        return expansion.getLogger();
    }

    public @Nullable Economy getEconomyHandler() {
        return this.economy;
    }

    public void setEconomyHandler(@NotNull Economy economy) {
        this.economy = economy;
    }

    public boolean setupEconomy() {
        try {
            ServicesManager servicesManager = Bukkit.getServicesManager();
            RegisteredServiceProvider<Economy> registration = servicesManager.getRegistration(Economy.class);
            if (registration == null) {
                Logger logger = getLogger();
                logger.warning("An economy plugin is not registered.");
                return false;
            }

            Plugin plugin = registration.getPlugin();
            PluginDescriptionFile description = plugin.getDescription();
            String fullName = description.getFullName();

            Economy economy = registration.getProvider();
            String economyName = economy.getName();

            Logger logger = getLogger();
            String messageFormat = "Successfully hooked into economy handler '%s' from plugin '%s'.";
            String logMessage = String.format(Locale.US, messageFormat, economyName, fullName);
            logger.info(logMessage);

            setEconomyHandler(economy);
            return true;
        } catch (Exception ex) {
            Logger logger = getLogger();
            logger.log(Level.WARNING, "Failed to find the economy handler:", ex);
            return false;
        }
    }
}

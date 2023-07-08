package combatlogx.expansion.compatibility.essentials;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishExpansion;
import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishHandler;

import combatlogx.expansion.compatibility.essentials.listener.ListenerEssentials;

public final class EssentialsExpansion extends VanishExpansion {
    private final EssentialsExpansionConfiguration configuration;

    private VanishHandler<?> vanishHandler;

    public EssentialsExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new EssentialsExpansionConfiguration();
        this.vanishHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        Logger logger = getLogger();
        if (checkDependency("Essentials", true)) {
            try {
                Class.forName("net.ess3.api.events.TPARequestEvent");
                logger.info("Detected EssentialsX successfully.");
                return true;
            } catch (ReflectiveOperationException ex) {
                logger.info("The installed Essentials plugin is not EssentialsX or is out of date.");
            }
        }

        return false;
    }

    @Override
    public void afterEnable() {
        new ListenerEssentials(this).register();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        ConfigurationManager configurationManager = getConfigurationManager();
        getEssentialsConfiguration().load(configurationManager.get("config.yml"));
    }

    @Override
    public @NotNull VanishHandler<?> getVanishHandler() {
        if (this.vanishHandler == null) {
            this.vanishHandler = new VanishHandlerEssentialsX(this);
        }

        return this.vanishHandler;
    }

    public @NotNull EssentialsExpansionConfiguration getEssentialsConfiguration() {
        return this.configuration;
    }
}

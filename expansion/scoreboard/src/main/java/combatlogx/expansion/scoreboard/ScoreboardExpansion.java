package combatlogx.expansion.scoreboard;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;

import combatlogx.expansion.scoreboard.manager.CustomScoreboardManager;

public final class ScoreboardExpansion extends Expansion {
    private final ScoreboardConfiguration configuration;
    private final CustomScoreboardManager scoreboardManager;
    private Boolean usePaperAPI;

    public ScoreboardExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new ScoreboardConfiguration();
        this.scoreboardManager = new CustomScoreboardManager(this);
        this.usePaperAPI = null;
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 8) {
            Logger logger = getLogger();
            logger.warning("This expansion requires Spigot 1.8.8 or higher.");
            selfDisable();
            return;
        }

        reloadConfig();

        ICombatLogX plugin = getPlugin();
        ITimerManager timerManager = plugin.getTimerManager();
        timerManager.addUpdaterTask(new ScoreboardUpdater(this));
    }

    @Override
    public void onDisable() {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 8) {
            return;
        }

        CustomScoreboardManager scoreboardManager = getScoreboardManager();
        scoreboardManager.removeAll();
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");

        ScoreboardConfiguration configuration = getConfiguration();
        YamlConfiguration yamlConfiguration = configurationManager.get("config.yml");
        configuration.load(yamlConfiguration);
    }

    public @NotNull CustomScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }

    public @NotNull ScoreboardConfiguration getConfiguration() {
        return this.configuration;
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    public boolean isPaperScoreboard() {
        if (this.usePaperAPI != null) {
            return this.usePaperAPI;
        }

        try {
            Class<?> componentClass = Class.forName("net.kyori.adventure.text.Component");
            Class<?> scoreboardClass = Class.forName("org.bukkit.scoreboard.Objective");
            Method displayNameMethod = scoreboardClass.getDeclaredMethod("displayName", componentClass);
            int modifiers = displayNameMethod.getModifiers();
            this.usePaperAPI = Modifier.isPublic(modifiers);
        } catch (ReflectiveOperationException ex) {
            this.usePaperAPI = false;
        }

        Logger logger = getLogger();
        if (this.usePaperAPI) {
            logger.info("Using Paper API and Adventure Component for scoreboards");
        } else {
            logger.info("Using Spigot and String for scoreboards.");
        }

        return this.usePaperAPI;
    }
}

package combatlogx.expansion.action.bar;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.adventure.adventure.text.TextComponent;
import com.github.sirblobman.api.adventure.adventure.text.TextReplacementConfig;
import com.github.sirblobman.api.adventure.adventure.text.format.TextColor;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.utility.paper.ComponentConverter;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

public final class ActionBarUpdater implements TimerUpdater {
    private final ActionBarExpansion expansion;

    public ActionBarUpdater(ActionBarExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    private ActionBarExpansion getExpansion() {
        return this.expansion;
    }

    private ActionBarConfiguration getConfiguration() {
        ActionBarExpansion expansion = getExpansion();
        return expansion.getConfiguration();
    }

    private ICombatLogX getCombatLogX() {
        ActionBarExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private LanguageManager getLanguageManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLanguageManager();
    }

    private PlayerDataManager getPlayerDataManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlayerDataManager();
    }

    private boolean isDebugModeDisabled() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.isDebugModeDisabled();
    }

    private void printDebug(String message) {
        if (isDebugModeDisabled()) {
            return;
        }

        Class<?> thisClass = getClass();
        String className = thisClass.getSimpleName();
        String logMessage = String.format(Locale.US, "[Debug] [%s] %s", className, message);

        Logger expansionLogger = getExpansion().getLogger();
        expansionLogger.info(logMessage);
    }

    @Override
    public void update(Player player, long timeLeftMillis) {
        if (isDisabled(player)) {
            return;
        }

        sendActionBar(player, timeLeftMillis);
    }

    @Override
    public void remove(Player player) {
        update(player, 0L);
    }

    private boolean isGlobalEnabled() {
        ActionBarConfiguration configuration = getConfiguration();
        return configuration.isEnabled();
    }

    private boolean isDisabled(Player player) {
        if (isGlobalEnabled()) {
            PlayerDataManager playerDataManager = getPlayerDataManager();
            YamlConfiguration playerData = playerDataManager.get(player);
            return !playerData.getBoolean("actionbar", true);
        }

        return true;
    }

    private void sendActionBar(Player player, long timeLeftMillis) {
        LanguageManager languageManager = getLanguageManager();
        if (timeLeftMillis <= 0) {
            String path = ("expansion.action-bar.ended");
            languageManager.sendActionBar(player, path, null);
            return;
        }

        Replacer replacer = message -> replacePlaceholders(player, message);
        Component preMessage = languageManager.getMessage(player, "expansion.action-bar.timer", replacer);

        TextReplacementConfig replacementConfig = getBarsReplacement(player, timeLeftMillis);
        Component message = preMessage.replaceText(replacementConfig);
        languageManager.sendActionBar(player, message);
    }

    private String replacePlaceholders(Player player, String message) {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();

        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return message;
        }

        List<Entity> enemyList = tagInformation.getEnemies();
        IPlaceholderManager placeholderManager = combatLogX.getPlaceholderManager();
        return placeholderManager.replaceAll(player, enemyList, message);
    }

    private TextReplacementConfig getBarsReplacement(Player player, long timeLeftMillis) {
        TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
        builder.matchLiteral("{bars}");
        builder.replacement(match -> getBars(player, timeLeftMillis));
        return builder.build();
    }

    private Component getBars(Player player, long timeLeftMillis) {
        printDebug("Bars Debug for player " + player.getName() + " and time left " + timeLeftMillis);

        ActionBarConfiguration configuration = getConfiguration();
        long scale = configuration.getScale();
        String leftSymbol = configuration.getLeftSymbol();
        String rightSymbol = configuration.getRightSymbol();
        TextColor leftColor = configuration.getLeftColor();
        TextColor rightColor = configuration.getRightColor();
        printDebug("Scale: " + scale);
        printDebug("Left Symbol: " + leftSymbol);
        printDebug("Right Symbol: " + rightSymbol);
        printDebug("Left Color: " + leftColor);
        printDebug("Right Color: " + rightColor);

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        long timerMaxSeconds = combatManager.getMaxTimerSeconds(player);
        printDebug("Max Timer: " + timerMaxSeconds);

        double timerMaxMillis = TimeUnit.SECONDS.toMillis(timerMaxSeconds);
        double scaleDouble = (double) scale;
        double timeLeftMillisDouble = (double) timeLeftMillis;

        double percent = clamp(timeLeftMillisDouble / timerMaxMillis);
        long leftBarsCount = Math.round(scaleDouble * percent);
        long rightBarsCount = (scale - leftBarsCount);
        printDebug("Percent: " + percent);
        printDebug("Left Bars: " + leftBarsCount);
        printDebug("Right Bars: " + rightBarsCount);

        TextComponent.Builder builder = Component.text();
        Component leftSymbolComponent = Component.text(leftSymbol, leftColor);
        Component rightSymbolComponent = Component.text(rightSymbol, rightColor);

        for (long i = 0; i < leftBarsCount; i++) {
            builder.append(leftSymbolComponent);
        }

        for(long i = 0; i < rightBarsCount; i++) {
            builder.append(rightSymbolComponent);
        }

        Component component = builder.build();
        printDebug("Final Component: " + ComponentConverter.shadedToGSON(component));
        return component;
    }

    private double clamp(double value) {
        return Math.max(0.0D, Math.min(value, 1.0D));
    }
}

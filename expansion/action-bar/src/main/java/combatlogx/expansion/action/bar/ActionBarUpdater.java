package combatlogx.expansion.action.bar;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.shaded.adventure.text.TextComponent;
import com.github.sirblobman.api.shaded.adventure.text.TextReplacementConfig;
import com.github.sirblobman.api.shaded.adventure.text.format.TextColor;
import com.github.sirblobman.api.utility.Validate;
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
            languageManager.sendActionBar(player, path);
            return;
        }

        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        IPlaceholderManager placeholderManager = combatLogX.getPlaceholderManager();
        Component message = languageManager.getMessage(player, "expansion.action-bar.timer");

        TextReplacementConfig replacementConfig = getBarsReplacement(player, timeLeftMillis);
        message = message.replaceText(replacementConfig);

        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation != null) {
            List<Entity> enemyList = tagInformation.getEnemies();
            Pattern placeholderPattern = Pattern.compile("\\{(\\S+)}");
            TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
            builder.match(placeholderPattern);
            builder.replacement((matchResult, builderCopy) -> {
                String placeholder = matchResult.group(1);
                Component replacement = placeholderManager.getPlaceholderReplacementComponent(player,
                        enemyList, placeholder);
                return (replacement == null ? Component.text(placeholder) : replacement);
            });

            TextReplacementConfig replacement = builder.build();
            message = message.replaceText(replacement);
        }

        languageManager.sendActionBar(player, message);
    }

    private TextReplacementConfig getBarsReplacement(Player player, long timeLeftMillis) {
        TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
        builder.matchLiteral("{bars}");
        builder.replacement(getBars(player, timeLeftMillis));
        return builder.build();
    }

    private Component getBars(Player player, long timeLeftMillis) {
        ActionBarConfiguration configuration = getConfiguration();
        long scale = configuration.getScale();
        String leftSymbol = configuration.getLeftSymbol();
        String rightSymbol = configuration.getRightSymbol();
        TextColor leftColor = configuration.getLeftColor();
        TextColor rightColor = configuration.getRightColor();

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        long timerMaxSeconds = combatManager.getMaxTimerSeconds(player);

        double timerMaxMillis = TimeUnit.SECONDS.toMillis(timerMaxSeconds);
        double scaleDouble = (double) scale;
        double timeLeftMillisDouble = (double) timeLeftMillis;

        double percent = clamp(timeLeftMillisDouble / timerMaxMillis);
        long leftBarsCount = Math.round(scaleDouble * percent);
        long rightBarsCount = (scale - leftBarsCount);

        TextComponent.Builder builder = Component.text();
        Component leftSymbolComponent = Component.text(leftSymbol, leftColor);
        Component rightSymbolComponent = Component.text(rightSymbol, rightColor);

        for (long i = 0; i < leftBarsCount; i++) {
            builder.append(leftSymbolComponent);
        }

        for (long i = 0; i < rightBarsCount; i++) {
            builder.append(rightSymbolComponent);
        }

        return builder.build();
    }

    private double clamp(double value) {
        return Math.max(0.0D, Math.min(value, 1.0D));
    }
}

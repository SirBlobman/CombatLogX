package combatlogx.expansion.logger.listener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.sirblobman.api.language.ComponentHelper;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerReTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.api.shaded.adventure.text.Component;

import combatlogx.expansion.logger.LoggerExpansion;
import combatlogx.expansion.logger.configuration.LogEntryOptions;
import combatlogx.expansion.logger.configuration.LogFileInfo;
import combatlogx.expansion.logger.configuration.LogOptions;
import combatlogx.expansion.logger.configuration.LogType;
import combatlogx.expansion.logger.configuration.LoggerConfiguration;

public final class ListenerLogger extends ExpansionListener {
    private final LoggerExpansion expansion;

    public ListenerLogger(LoggerExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    private @NotNull LoggerExpansion getLoggerExpansion() {
        return this.expansion;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void beforeTag(PlayerPreTagEvent e) {
        if (isDisabled(LogType.PRE_TAG)) {
            return;
        }

        Player player = e.getPlayer();
        Entity enemy = e.getEnemy();
        TagReason tagReason = e.getTagReason();
        TagType tagType = e.getTagType();

        String format = getFormat(LogType.PRE_TAG);
        String playerName = player.getName();
        String enemyName = getEntityName(enemy);
        String tagReasonName = tagReason.name();
        String tagTypeName = tagType.name();
        String cancelledString = Boolean.toString(e.isCancelled());

        String message = format.replace("{player}", playerName).replace("{enemy}", enemyName)
                .replace("{tag_reason}", tagReasonName).replace("{tag_type}", tagTypeName)
                .replace("{was_cancelled}", cancelledString);
        appendLog(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTag(PlayerTagEvent e) {
        if (isDisabled(LogType.TAG)) {
            return;
        }

        Player player = e.getPlayer();
        Entity enemy = e.getEnemy();
        TagReason tagReason = e.getTagReason();
        TagType tagType = e.getTagType();

        String format = getFormat(LogType.TAG);
        String playerName = player.getName();
        String enemyName = getEntityName(enemy);
        String tagReasonName = tagReason.name();
        String tagTypeName = tagType.name();

        String message = format.replace("{player}", playerName).replace("{enemy}", enemyName)
                .replace("{tag_reason}", tagReasonName).replace("{tag_type}", tagTypeName);
        appendLog(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onReTag(PlayerReTagEvent e) {
        if (isDisabled(LogType.RE_TAG)) {
            return;
        }

        Player player = e.getPlayer();
        Entity enemy = e.getEnemy();
        TagReason tagReason = e.getTagReason();
        TagType tagType = e.getTagType();

        String format = getFormat(LogType.RE_TAG);
        String playerName = player.getName();
        String enemyName = getEntityName(enemy);
        String tagReasonName = tagReason.name();
        String tagTypeName = tagType.name();
        String cancelledString = Boolean.toString(e.isCancelled());

        String message = format.replace("{player}", playerName).replace("{enemy}", enemyName)
                .replace("{tag_reason}", tagReasonName).replace("{tag_type}", tagTypeName)
                .replace("{was_cancelled}", cancelledString);
        appendLog(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUntag(PlayerUntagEvent e) {
        if (isDisabled(LogType.UNTAG)) {
            return;
        }

        Player player = e.getPlayer();
        UntagReason untagReason = e.getUntagReason();
        boolean isExpire = untagReason.isExpire();

        String format = getFormat(LogType.UNTAG);
        String playerName = player.getName();
        String untagReasonName = untagReason.name();
        String expireString = Boolean.toString(isExpire);

        String message = format.replace("{player}", playerName).replace("{untag_reason}", untagReasonName)
                .replace("{was_expire}", expireString);
        appendLog(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPunish(PlayerPunishEvent e) {
        if (isDisabled(LogType.PUNISH)) {
            return;
        }

        Player player = e.getPlayer();
        List<Entity> enemyList = e.getEnemies();
        UntagReason untagReason = e.getPunishReason();

        String format = getFormat(LogType.PUNISH);
        String playerName = player.getName();
        String enemyNames = enemyList.stream().map(this::getEntityName).collect(Collectors.joining(", "));
        String untagReasonName = untagReason.name();
        String cancelledString = Boolean.toString(e.isCancelled());

        String message = format.replace("{player}", playerName).replace("{enemy}", enemyNames)
                .replace("{punish_reason}", untagReasonName).replace("{was_cancelled}", cancelledString);
        appendLog(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (isDisabled(LogType.ENTITY_DAMAGE_EVENT)) {
            return;
        }

        Entity damaged = e.getEntity();
        Entity damager = e.getDamager();

        String damagedType = damaged.getType().name();
        String damagedName = getEntityName(damaged);
        String damagerType = damager.getType().name();
        String damagerName = getEntityName(damager);
        String wasCancelled = Boolean.toString(e.isCancelled());

        String format = getFormat(LogType.ENTITY_DAMAGE_EVENT);
        String message = format.replace("{damaged_type}", damagedType)
                .replace("{damaged_name}", damagedName).replace("{damager_type}", damagerType)
                .replace("{damager_name}", damagerName).replace("{was_cancelled}", wasCancelled);
        appendLog(message);
    }

    private @NotNull String getEntityName(@Nullable Entity entity) {
        if (entity == null) {
            CommandSender console = Bukkit.getConsoleSender();
            LanguageManager languageManager = getLanguageManager();
            Component message = languageManager.getMessage(console, "placeholder.unknown-enemy");
            return ComponentHelper.toPlain(message);
        }

        ICombatLogX combatLogX = getCombatLogX();
        MultiVersionHandler multiVersionHandler = combatLogX.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(entity);
    }

    private boolean isDisabled(@NotNull LogType logType) {
        LoggerExpansion expansion = getLoggerExpansion();
        LoggerConfiguration configuration = expansion.getConfiguration();
        LogOptions logOptions = configuration.getLogOptions();
        return logType.isEnabled(logOptions);
    }

    private @NotNull String getFormat(@NotNull LogType logType) {
        LoggerExpansion expansion = getLoggerExpansion();
        LoggerConfiguration configuration = expansion.getConfiguration();
        LogEntryOptions logEntryOptions = configuration.getLogEntryOptions();
        String baseFormat = logType.getFormat(logEntryOptions);

        String prefix = logEntryOptions.getCurrentPrefix();
        return (prefix + " " + baseFormat);
    }

    private @NotNull File getLogFile() {
        LoggerExpansion expansion = getLoggerExpansion();
        LoggerConfiguration configuration = expansion.getConfiguration();
        LogFileInfo logFileInfo = configuration.getLogFileInfo();

        File dataFolder = expansion.getDataFolder();
        return logFileInfo.getCurrentLogFile(dataFolder);
    }

    private void appendLog(String @NotNull ... messageArray) {
        try {
            LoggerExpansion expansion = getLoggerExpansion();
            File dataFolder = expansion.getDataFolder();
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                throw new IOException("Failed to create expansion folder.");
            }

            File logFile = getLogFile();
            if (!logFile.exists() && !logFile.createNewFile()) {
                throw new IOException("Failed to create custom log file.");
            }

            Path logPath = logFile.toPath();
            List<String> messageList = Arrays.asList(messageArray);
            Files.write(logPath, messageList, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch(IOException ex) {
            Logger logger = getExpansionLogger();
            logger.log(Level.WARNING, "Failed to write to a custom log file:", ex);
        }
    }
}

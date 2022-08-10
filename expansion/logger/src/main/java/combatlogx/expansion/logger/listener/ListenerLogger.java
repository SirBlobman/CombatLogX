package combatlogx.expansion.logger.listener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerReTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

import combatlogx.expansion.logger.LoggerExpansion;

public final class ListenerLogger extends ExpansionListener {
    private final Pattern fileNameRegex;

    public ListenerLogger(LoggerExpansion expansion) {
        super(expansion);
        this.fileNameRegex = Pattern.compile("[^\\w.\\-]");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void beforeTag(PlayerPreTagEvent e) {
        if (isDisabled("log-pretag")) {
            return;
        }

        Player player = e.getPlayer();
        Entity enemy = e.getEnemy();
        TagReason tagReason = e.getTagReason();
        TagType tagType = e.getTagType();

        String format = getLoggerFormat("pretag-format");
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
        if (isDisabled("log-tag")) {
            return;
        }

        Player player = e.getPlayer();
        Entity enemy = e.getEnemy();
        TagReason tagReason = e.getTagReason();
        TagType tagType = e.getTagType();

        String format = getLoggerFormat("tag-format");
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
        if (isDisabled("log-retag")) {
            return;
        }

        Player player = e.getPlayer();
        Entity enemy = e.getEnemy();
        TagReason tagReason = e.getTagReason();
        TagType tagType = e.getTagType();

        String format = getLoggerFormat("retag-format");
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
        if (isDisabled("log-untag")) {
            return;
        }

        Player player = e.getPlayer();
        UntagReason untagReason = e.getUntagReason();
        boolean isExpire = untagReason.isExpire();

        String format = getLoggerFormat("untag-format");
        String playerName = player.getName();
        String untagReasonName = untagReason.name();
        String expireString = Boolean.toString(isExpire);

        String message = format.replace("{player}", playerName).replace("{untag_reason}", untagReasonName)
                .replace("{was_expire}", expireString);
        appendLog(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPunish(PlayerPunishEvent e) {
        if (isDisabled("log-punish")) {
            return;
        }

        Player player = e.getPlayer();
        List<Entity> enemyList = e.getEnemies();
        UntagReason untagReason = e.getPunishReason();

        String format = getLoggerFormat("untag-format");
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
        if (isDisabled("log-entity-damage-event")) {
            return;
        }

        Entity damaged = e.getEntity();
        Entity damager = e.getDamager();

        String damagedType = damaged.getType().name();
        String damagedName = getEntityName(damaged);
        String damagerType = damager.getType().name();
        String damagerName = getEntityName(damager);
        String wasCancelled = Boolean.toString(e.isCancelled());

        String format = getLoggerFormat("entity-damage-event-format");
        String message = format.replace("{damaged_type}", damagedType)
                .replace("{damaged_name}", damagedName).replace("{damager_type}", damagerType)
                .replace("{damager_name}", damagerName).replace("{was_cancelled}", wasCancelled);
        appendLog(message);
    }

    private boolean isDisabled(String path) {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return !configuration.getBoolean("log-options." + path);
    }

    private String getEntityName(Entity entity) {
        if (entity == null) {
            CommandSender console = Bukkit.getConsoleSender();
            LanguageManager languageManager = getLanguageManager();
            String message = languageManager.getMessageString(console, "placeholder.unknown-enemy", null);
            return MessageUtility.color(message);
        }

        ICombatLogX combatLogX = getCombatLogX();
        MultiVersionHandler multiVersionHandler = combatLogX.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(entity);
    }

    private String getLoggerFormat(String path) {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        String prefixFormat = configuration.getString("log-entry-options.prefix-format");
        if (prefixFormat == null) {
            prefixFormat = "[MMMM dd, YYYY HH:mm:ss.SSSa zzz] ";
        }

        SimpleDateFormat format = new SimpleDateFormat(prefixFormat);
        String prefix = format.format(new Date(System.currentTimeMillis()));

        String messageFormat = configuration.getString("log-entry-options." + path);
        if (messageFormat == null) {
            messageFormat = "";
        }

        return (prefix + messageFormat);
    }

    private String getLogFileName() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        String fileNameOption = configuration.getString("log-file-info.file-name");
        if (fileNameOption == null) fileNameOption = "logger";

        String fileExtraFormatOption = configuration.getString("log-file-info.file-extra.format");
        if (fileExtraFormatOption == null) fileExtraFormatOption = "yyyy.MM.dd";

        String fileExtensionOption = configuration.getString("log-file-info.file-extension");
        if (fileExtensionOption == null) fileExtensionOption = "log";

        SimpleDateFormat format = new SimpleDateFormat(fileExtraFormatOption);
        Date currentDate = new Date(System.currentTimeMillis());
        String fileNameExtra = format.format(currentDate);

        String preFileName = (fileNameOption + "-" + fileNameExtra + "." + fileExtensionOption);
        Matcher matcher = this.fileNameRegex.matcher(preFileName);
        return matcher.replaceAll("_");
    }

    private void appendLog(String... messageArray) {
        Expansion expansion = getExpansion();
        Logger logger = expansion.getLogger();
        try {
            File dataFolder = expansion.getDataFolder();
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                logger.warning("Could not create expansion folder!");
                return;
            }

            String logFileName = getLogFileName();
            File logFile = new File(dataFolder, logFileName);
            if (!logFile.exists() && !logFile.createNewFile()) {
                logger.warning("Could not create log file!");
                return;
            }

            Path logPath = logFile.toPath();
            List<String> messageList = Arrays.asList(messageArray);
            Files.write(logPath, messageList, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "An error occurred while appending a message to a log file:", ex);
        }
    }
}

package com.SirBlobman.combatlogx.expansion.logger.listener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.*;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagReason;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.api.shaded.nms.AbstractNMS;
import com.SirBlobman.combatlogx.api.shaded.nms.EntityHandler;
import com.SirBlobman.combatlogx.api.shaded.nms.MultiVersionHandler;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;
import com.SirBlobman.combatlogx.expansion.logger.LoggerExpansion;

public class ListenerLogger implements Listener {
    private final LoggerExpansion expansion;
    public ListenerLogger(LoggerExpansion expansion) {
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void beforeTag(PlayerPreTagEvent e) {
        if(isDisabled("log-pretag")) return;
        String format = getLoggerFormat("pretag-format");
        if(format == null) return;

        Player player = e.getPlayer();
        LivingEntity enemy = e.getEnemy();
        TagReason tagReason = e.getTagReason();
        TagType tagType = e.getTagType();

        String playerName = player.getName();
        String enemyName = getEntityName(enemy);
        String tagReasonString = tagReason.name();
        String tagTypeString = tagType.name();
        String wasCancelled = Boolean.toString(e.isCancelled());

        String message = format.replace("{player_name}", playerName).replace("{enemy_name}", enemyName)
                .replace("{tag_reason}", tagReasonString).replace("{tag_type}", tagTypeString)
                .replace("{was_cancelled}", wasCancelled);
        appendLog(message);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onTag(PlayerTagEvent e) {
        if(isDisabled("log-tag")) return;
        String format = getLoggerFormat("tag-format");
        if(format == null) return;

        Player player = e.getPlayer();
        LivingEntity enemy = e.getEnemy();
        TagReason tagReason = e.getTagReason();
        TagType tagType = e.getTagType();

        String playerName = player.getName();
        String enemyName = getEntityName(enemy);
        String tagReasonString = tagReason.name();
        String tagTypeString = tagType.name();

        String message = format.replace("{player_name}", playerName).replace("{enemy_name}", enemyName)
                .replace("{tag_reason}", tagReasonString).replace("{tag_type}", tagTypeString);
        appendLog(message);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onReTag(PlayerReTagEvent e) {
        if(isDisabled("log-retag")) return;
        String format = getLoggerFormat("retag-format");
        if(format == null) return;

        Player player = e.getPlayer();
        LivingEntity enemy = e.getEnemy();
        TagReason tagReason = e.getTagReason();
        TagType tagType = e.getTagType();

        String playerName = player.getName();
        String enemyName = getEntityName(enemy);
        String tagReasonString = tagReason.name();
        String tagTypeString = tagType.name();

        String message = format.replace("{player_name}", playerName).replace("{enemy_name}", enemyName)
                .replace("{tag_reason}", tagReasonString).replace("{tag_type}", tagTypeString);
        appendLog(message);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onUntag(PlayerUntagEvent e) {
        if(isDisabled("log-untag")) return;
        String format = getLoggerFormat("untag-format");
        if(format == null) return;

        Player player = e.getPlayer();
        UntagReason reason = e.getUntagReason();
        boolean expire = reason.isExpire();

        String playerName = player.getName();
        String untagReasonString = reason.name();
        String wasExpire = Boolean.toString(expire);

        String message = format.replace("{player_name}", playerName)
                .replace("{untag_reason}", untagReasonString).replace("{was_expire}", wasExpire);
        appendLog(message);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPunish(PlayerPunishEvent e) {
        if(isDisabled("log-punish")) return;
        String format = getLoggerFormat("punish-format");
        if(format == null) return;

        Player player = e.getPlayer();
        UntagReason reason = e.getPunishReason();

        String playerName = player.getName();
        String punishReasonString = reason.name();
        String wasCancelled = Boolean.toString(e.isCancelled());

        String message = format.replace("{player_name}", playerName)
                .replace("{punish_reason}", punishReasonString).replace("{was_cancelled}", wasCancelled);
        appendLog(message);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onCombat(EntityDamageByEntityEvent e) {
        if(isDisabled("log-entity-damage-event")) return;
        String format = getLoggerFormat("entity-damage-event-format");
        if(format == null) return;

        String wasCancelled = Boolean.toString(e.isCancelled());

        Entity damaged = e.getEntity();
        String damagedType = damaged.getType().name();
        String damagedName = getEntityName(damaged);

        Entity damager = e.getDamager();
        String damagerType = damager.getType().name();
        String damagerName = getEntityName(damager);

        String message = format.replace("{damaged_type}", damagedType)
                .replace("{damaged_name}", damagedName).replace("{damager_type}", damagerType)
                .replace("{damager_name}", damagerName).replace("{was_cancelled}", wasCancelled);
        appendLog(message);
    }

    private String getEntityName(Entity entity) {
        ICombatLogX plugin = this.expansion.getPlugin();
        ILanguageManager languageManager = plugin.getLanguageManager();
        if(entity == null) return languageManager.getMessage("errors.unknown-entity-name");
    
        MultiVersionHandler<?> multiVersionHandler = plugin.getMultiVersionHandler();
        AbstractNMS nmsHandler = multiVersionHandler.getInterface();
        EntityHandler entityHandler = nmsHandler.getEntityHandler();
        return entityHandler.getName(entity);
    }

    private String getLoggerFormat(String path) {
        FileConfiguration config = this.expansion.getConfig("logger.yml");
        if(config == null) return null;

        String prefixFormat = config.getString("log-entry-options.prefix-format");
        if(prefixFormat == null) prefixFormat = "[MMMM dd, YYYY HH:mm:ss.SSSa zzz] ";

        SimpleDateFormat format = new SimpleDateFormat(prefixFormat);
        String prefix = format.format(new Date(System.currentTimeMillis()));

        String messageFormat = config.getString("log-entry-options." + path);
        if(messageFormat == null) messageFormat = "";

        return (prefix + messageFormat);
    }

    private boolean isDisabled(String path) {
        FileConfiguration config = this.expansion.getConfig("logger.yml");
        if(config == null) return true;

        return !config.getBoolean("log-options." + path);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void appendLog(String message) {
        try {
            File dataFolder = this.expansion.getDataFolder();
            if(!dataFolder.exists()) dataFolder.mkdirs();

            String fileName = this.expansion.getLogFileName();
            File logFile = new File(dataFolder, fileName);
            if(!logFile.exists()) logFile.createNewFile();

            Path path = logFile.toPath();
            Set<String> messageList = Collections.singleton(message);
            Files.write(path, messageList, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch(IOException ex) {
            Logger logger = this.expansion.getLogger();
            logger.log(Level.SEVERE, "An error occurred while trying to append to the log file.", ex);
        }
    }
}
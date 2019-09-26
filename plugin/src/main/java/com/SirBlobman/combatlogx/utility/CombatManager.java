package com.SirBlobman.combatlogx.utility;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.api.utility.Util;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.*;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class CombatManager implements ICombatManager, Runnable {
    private final ICombatLogX plugin;
    public CombatManager(ICombatLogX plugin) {
        this.plugin = plugin;
    }

    private static final Map<UUID, Long> uuidToExpireTime = Util.newMap();
    private static final Map<UUID, UUID> uuidToEnemy = Util.newMap();

    @Override
    public boolean tag(Player player, LivingEntity enemy, PlayerPreTagEvent.TagType tagType, PlayerPreTagEvent.TagReason tagReason) {
        if(player == null || tagType == null || tagReason == null) return false;
        if(failsPreTagEvent(player, enemy, tagType, tagReason)) return false;
        boolean wasInCombat = isInCombat(player);

        FileConfiguration config = this.plugin.getConfig("config.yml");
        long systemMillis = System.currentTimeMillis();
        long timerMillis = (1000L * config.getLong("combat.timer"));
        long endMillis = (systemMillis + timerMillis);

        PlayerTagEvent tagEvent = new PlayerTagEvent(player, enemy, tagType, tagReason, endMillis);
        PluginManager manager = Bukkit.getPluginManager();
        manager.callEvent(tagEvent);
        endMillis = tagEvent.getEndTime();

        if(!wasInCombat) sendTagMessage(player, enemy, tagType, tagReason);

        UUID uuid = player.getUniqueId();
        uuidToExpireTime.put(uuid, endMillis);

        if(enemy == null) uuidToEnemy.putIfAbsent(uuid, null);
        else uuidToEnemy.put(uuid, enemy.getUniqueId());

        return true;
    }

    @Override
    public void untag(Player player, PlayerUntagEvent.UntagReason untagReason) {
        if(player == null | untagReason == null || !isInCombat(player)) return;

        UUID uuid = player.getUniqueId();
        uuidToExpireTime.remove(uuid);

        UUID previousEnemyId = uuidToEnemy.remove(uuid);
        Entity previousEnemyEntity = (previousEnemyId != null ? Bukkit.getEntity(previousEnemyId) : null);
        LivingEntity previousEnemy = (previousEnemyEntity instanceof LivingEntity ? (LivingEntity) previousEnemyEntity : null);

        PlayerUntagEvent untagEvent = new PlayerUntagEvent(player, untagReason, previousEnemy);

        PluginManager manager = Bukkit.getPluginManager();
        manager.callEvent(untagEvent);
    }

    @Override
    public boolean isInCombat(Player player) {
        if(player == null) return false;

        UUID uuid = player.getUniqueId();
        return uuidToExpireTime.containsKey(uuid);
    }

    @Override
    public List<Player> getPlayersInCombat() {
        List<Player> playerList = Util.newList();

        List<UUID> uuidList = Util.newList(uuidToExpireTime.keySet());
        for(UUID uuid : uuidList) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                uuidToExpireTime.remove(uuid);
                continue;
            }
            playerList.add(player);
        }

        return playerList;
    }

    @Override
    public LivingEntity getEnemy(Player player) {
        if(player == null) return null;

        UUID uuid = player.getUniqueId();
        UUID enemyId = uuidToEnemy.getOrDefault(uuid, null);
        Entity enemyEntity = (enemyId != null ? Bukkit.getEntity(enemyId) : null);
        return (enemyEntity instanceof LivingEntity ? (LivingEntity) enemyEntity : null);
    }

    @Override
    public OfflinePlayer getByEnemy(LivingEntity enemy) {
        if(enemy == null) return null;

        UUID enemyId = enemy.getUniqueId();
        for(Map.Entry<UUID, UUID> entry : uuidToEnemy.entrySet()) {
            UUID linkedEnemyId = entry.getValue();
            if(!enemyId.equals(linkedEnemyId)) continue;

            UUID playerId = entry.getKey();
            return Bukkit.getOfflinePlayer(playerId);
        }

        return null;
    }

    @Override
    public int getTimerSecondsLeft(Player player) {
        if(!isInCombat(player)) return -1;

        long millisLeft = getTimerMillisLeft(player);
        return (int) (millisLeft / 1000);
    }

    @Override
    public long getTimerMillisLeft(Player player) {
        if(!isInCombat(player)) return -1;

        UUID uuid = player.getUniqueId();
        long combatEnds = uuidToExpireTime.get(uuid);
        long systemMillis = System.currentTimeMillis();
        return (combatEnds - systemMillis);
    }

    @Override
    public boolean punish(Player player, PlayerUntagEvent.UntagReason punishReason, LivingEntity previousEnemy) {
        PlayerPunishEvent punishEvent = new PlayerPunishEvent(player, punishReason, previousEnemy);
        PluginManager manager = Bukkit.getPluginManager();

        manager.callEvent(punishEvent);
        if(punishEvent.isCancelled()) return false;

        runPunishCommands(player, previousEnemy);
        return true;
    }

    @Override
    public void run() {
        PluginManager manager = Bukkit.getPluginManager();
        List<Player> combatPlayerList = getPlayersInCombat();
        for(Player player : combatPlayerList) {
            int timeLeft = getTimerSecondsLeft(player);
            if(timeLeft <= 0) untag(player, PlayerUntagEvent.UntagReason.EXPIRE);

            PlayerCombatTimerChangeEvent event = new PlayerCombatTimerChangeEvent(player, timeLeft);
            manager.callEvent(event);
        }
    }

    private boolean failsPreTagEvent(Player player, LivingEntity enemy, PlayerPreTagEvent.TagType tagType, PlayerPreTagEvent.TagReason tagReason) {
        PluginManager manager = Bukkit.getPluginManager();
        PlayerPreTagEvent event = new PlayerPreTagEvent(player, enemy, tagType, tagReason);

        manager.callEvent(event);
        return event.isCancelled();
    }

    private void sendTagMessage(Player player, LivingEntity enemy, PlayerPreTagEvent.TagType tagType, PlayerPreTagEvent.TagReason tagReason) {
        if(tagType == PlayerPreTagEvent.TagType.UNKNOWN || tagReason == PlayerPreTagEvent.TagReason.UNKNOWN) {
            String message = this.plugin.getLanguageMessageColored("tag-messages.unknown");
            this.plugin.sendMessage(player, message);
        }

        String enemyType = (enemy == null ? EntityType.UNKNOWN.name() : enemy.getType().name());
        String enemyName = getEntityName(enemy);

        if(tagType == PlayerPreTagEvent.TagType.MOB) {
            String messagePath = "tag-messages." + (tagReason == PlayerPreTagEvent.TagReason.ATTACKER ? "attacker.of-" : "attacked.by-") + "mob";

            String message = this.plugin.getLanguageMessageColored(messagePath).replace("{mob_type}", enemyType).replace("{name}", enemyName);
            this.plugin.sendMessage(player, message);
        }

        if(tagType == PlayerPreTagEvent.TagType.MOB) {
            String messagePath = "tag-messages." + (tagReason == PlayerPreTagEvent.TagReason.ATTACKER ? "attacker.of-" : "attacked.by-") + "player";

            String message = this.plugin.getLanguageMessageColored(messagePath).replace("{mob_type}", enemyType).replace("{name}", enemyName);
            this.plugin.sendMessage(player, message);
        }
    }

    private String getEntityName(LivingEntity enemy) {
        if(enemy == null) return this.plugin.getLanguageMessage("erros.unknown-entity-name");

        int minorVersion = NMS_Handler.getMinorVersion();
        if(minorVersion <= 7) {
            if(enemy instanceof Player) {
                Player player = (Player) enemy;
                return player.getName();
            }

            EntityType type = enemy.getType();
            return type.name();
        }

        return enemy.getName();
    }

    private void runPunishCommands(Player player, LivingEntity previousEnemy) {
        FileConfiguration config = this.plugin.getConfig("config.yml");
        List<String> punishCommandList = config.getStringList("punishments.punish-command-list");
        for(String punishCommand : punishCommandList) {
            String sudoCommand = getSudoCommand(player, previousEnemy, punishCommand);
            if(sudoCommand.startsWith("[PLAYER]")) {
                String command = sudoCommand.substring("[PLAYER]".length());
                player.performCommand(command);
                continue;
            }

            if(sudoCommand.startsWith("[OP]")) {
                String command = sudoCommand.substring("[OP]".length());
                if(player.isOp()) {
                    player.performCommand(command);
                    continue;
                }

                player.setOp(true);
                player.performCommand(command);
                player.setOp(false);

                continue;
            }

            CommandSender console = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(console, sudoCommand);
        }
    }

    @Override
    public String getSudoCommand(Player player, LivingEntity enemy, String command) {
        command = command.replace("{player}", player.getName()).replace("{enemy}", getEntityName(enemy));

        try {
            PluginManager manager = Bukkit.getPluginManager();
            if(manager.isPluginEnabled("PlaceholderAPI")) {
                Class<?> class_PlaceholderAPI = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                Method method_setPlaceholders = class_PlaceholderAPI.getDeclaredMethod("setPlaceholders", OfflinePlayer.class, String.class);
                command = (String) method_setPlaceholders.invoke(null, player, command);
            }

            if(manager.isPluginEnabled("MVdWPlaceholderAPI")) {
                Class<?> class_PlaceholderAPI = Class.forName("be.maximvdw.placeholderapi.PlaceholderAPI");
                Method method_replacePlaceholders = class_PlaceholderAPI.getDeclaredMethod("replacePlaceholders", OfflinePlayer.class, String.class);
                command = (String) method_replacePlaceholders.invoke(null, player, command);
            }
        } catch(ReflectiveOperationException ignored) {}

        return command;
    }
}
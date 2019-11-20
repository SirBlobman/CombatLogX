package com.SirBlobman.combatlogx.expansion.notifier.utility;

import java.util.List;
import java.util.UUID;

import com.SirBlobman.combatlogx.api.shaded.nms.NMS_Handler;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.shaded.utility.MessageUtil;
import com.SirBlobman.combatlogx.api.shaded.utility.Util;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.notifier.Notifier;
import com.SirBlobman.combatlogx.utility.PlaceholderReplacer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class ActionBarHandler {
    private static final List<UUID> noActionBarList = Util.newList();
    public static void toggle(Player player) {
        if(player == null) return;

        UUID uuid = player.getUniqueId();
        if(noActionBarList.contains(uuid)) {
            noActionBarList.remove(uuid);
            return;
        }

        noActionBarList.add(uuid);
    }

    public static boolean isDisabled(Player player) {
        if(player == null) return true;

        UUID uuid = player.getUniqueId();
        return noActionBarList.contains(uuid);
    }

    public static void updateActionBar(Notifier expansion, Player player) {
        if(player == null) return;

        FileConfiguration config = expansion.getConfig("actionbar.yml");
        String message = MessageUtil.color(replacePlaceholders(expansion, player, config.getString("message-timer")));

        NMS_Handler handler = NMS_Handler.getHandler();
        handler.sendActionBar(player, message);
    }

    public static void removeActionBar(Notifier expansion, Player player) {
        if(player == null) return;

        FileConfiguration config = expansion.getConfig("actionbar.yml");
        String message = MessageUtil.color(config.getString("message-ended"));

        NMS_Handler handler = NMS_Handler.getHandler();
        handler.sendActionBar(player, message);
    }

    private static String replacePlaceholders(Notifier expansion, Player player, String string) {
        if(player == null) return string;
        ICombatLogX plugin = expansion.getPlugin();

        String timeLeft = PlaceholderReplacer.getTimeLeftSeconds(plugin, player);
        String inCombat = PlaceholderReplacer.getInCombat(plugin, player);
        String combatStatus = PlaceholderReplacer.getCombatStatus(plugin, player);

        String enemyName = PlaceholderReplacer.getEnemyName(plugin, player);
        String enemyHealth = PlaceholderReplacer.getEnemyHealth(plugin, player);
        String enemyHealthRounded = PlaceholderReplacer.getEnemyHealthRounded(plugin, player);
        String enemyHearts = PlaceholderReplacer.getEnemyHearts(plugin, player);

        String barsLeft = getBarsLeft(expansion, player);
        String barsRight = getBarsRight(expansion, player);

        return string.replace("{time_left}", timeLeft)
                .replace("{in_combat}", inCombat)
                .replace("{status}", combatStatus)
                .replace("{enemy_name}", enemyName)
                .replace("{enemy_health}", enemyHealth)
                .replace("{enemy_health_rounded}", enemyHealthRounded)
                .replace("{enemy_hearts}", enemyHearts)
                .replace("{bars_left}", barsLeft)
                .replace("{bars_right}", barsRight);
    }

    private static String getBarsLeft(Notifier expansion, Player player) {
        ICombatLogX plugin = expansion.getPlugin();
        FileConfiguration config = plugin.getConfig("config.yml");
        int timer = config.getInt("combat.timer");

        ICombatManager manager = plugin.getCombatManager();
        int timeLeft = manager.getTimerSecondsLeft(player);
        int right = (timer - timeLeft);
        int left = (timer - right);

        FileConfiguration actionbar = expansion.getConfig("actionbar.yml");
        StringBuilder builder = new StringBuilder(actionbar.getString("bars-left-color"));
        for(int i = 0; i < left; i++) {
            builder.append("|");
        }
        return MessageUtil.color(builder.toString());
    }

    private static String getBarsRight(Notifier expansion, Player player) {
        ICombatLogX plugin = expansion.getPlugin();
        FileConfiguration config = plugin.getConfig("config.yml");
        int timer = config.getInt("combat.timer");

        ICombatManager manager = plugin.getCombatManager();
        int timeLeft = manager.getTimerSecondsLeft(player);
        int right = (timer - timeLeft);

        FileConfiguration actionbar = expansion.getConfig("actionbar.yml");
        StringBuilder builder = new StringBuilder(actionbar.getString("bars-right-color"));
        for(int i = 0; i < right; i++) {
            builder.append("|");
        }
        return MessageUtil.color(builder.toString());
    }
}
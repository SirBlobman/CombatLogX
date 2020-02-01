package com.SirBlobman.combatlogx.expansion.notifier.utility;

import java.util.List;
import java.util.UUID;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.shaded.nms.NMS_Handler;
import com.SirBlobman.combatlogx.api.shaded.nms.boss.bar.BossBarHandler;
import com.SirBlobman.combatlogx.api.shaded.utility.MessageUtil;
import com.SirBlobman.combatlogx.api.shaded.utility.Util;
import com.SirBlobman.combatlogx.expansion.notifier.Notifier;
import com.SirBlobman.combatlogx.utility.PlaceholderReplacer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class BossBarManager {
    private static final List<UUID> noBossBarList = Util.newList();

    public static void toggle(Player player) {
        if(player == null) return;

        UUID uuid = player.getUniqueId();
        if(noBossBarList.contains(uuid)) {
            noBossBarList.remove(uuid);
            return;
        }

        noBossBarList.add(uuid);
    }

    public static boolean isDisabled(Player player) {
        if(player == null) return true;

        UUID uuid = player.getUniqueId();
        return noBossBarList.contains(uuid);
    }

    public static void updateBossBar(Notifier expansion, Player player) {
        if(player == null || expansion == null) return;
        FileConfiguration bossbar = expansion.getConfig("bossbar.yml");
        if(!bossbar.getBoolean("enabled")) return;
        
        String message = MessageUtil.color(replacePlaceholders(expansion, player, bossbar.getString("message-timer")));
        String color = bossbar.getString("bar-color");
        String style = bossbar.getString("bar-style");

        ICombatLogX plugin = expansion.getPlugin();
        FileConfiguration config = plugin.getConfig("config.yml");
        double timer = config.getInt("combat.timer");
        double timeLeft = plugin.getCombatManager().getTimerSecondsLeft(player);
        if(timeLeft <= 0) timeLeft = 0;
        double progress = (timeLeft / timer);

        NMS_Handler handler = NMS_Handler.getHandler();
        BossBarHandler bossBarHandler = handler.getBossBarHandler();
        bossBarHandler.updateBossBar(player, message, progress, color, style);
    }

    public static void removeBossBar(Notifier expansion, Player player, boolean shutdown) {
        if(player == null || expansion == null) return;
        NMS_Handler handler = NMS_Handler.getHandler();
        BossBarHandler bossBarHandler = handler.getBossBarHandler();
        
        if(shutdown) {
            bossBarHandler.removeBossBar(player);
            return;
        }

        FileConfiguration bossbar = expansion.getConfig("bossbar.yml");
        String message = bossbar.getString("message-ended");
        if(message == null) message = "";
        
        String title = MessageUtil.color(message);
        String color = bossbar.getString("bar-color");
        String style = bossbar.getString("bar-style");
        double progress = 0.0D;

        bossBarHandler.updateBossBar(player, title, progress, color, style);

        BukkitScheduler scheduler = Bukkit.getScheduler();
        JavaPlugin plugin = expansion.getPlugin().getPlugin();
        scheduler.runTaskLater(plugin, () -> bossBarHandler.removeBossBar(player), 20L);
    }

    private static String replacePlaceholders(Notifier expansion, Player player, String string) {
        ICombatLogX plugin = expansion.getPlugin();
        if(player == null) return string;

        String timeLeft = PlaceholderReplacer.getTimeLeftSeconds(plugin, player);
        String inCombat = PlaceholderReplacer.getInCombat(plugin, player);
        String combatStatus = PlaceholderReplacer.getCombatStatus(plugin, player);

        String enemyName = PlaceholderReplacer.getEnemyName(plugin, player);
        String enemyHealth = PlaceholderReplacer.getEnemyHealth(plugin, player);
        String enemyHealthRounded = PlaceholderReplacer.getEnemyHealthRounded(plugin, player);
        String enemyHearts = PlaceholderReplacer.getEnemyHearts(plugin, player);

        return string.replace("{time_left}", timeLeft)
                .replace("{in_combat}", inCombat)
                .replace("{status}", combatStatus)
                .replace("{enemy_name}", enemyName)
                .replace("{enemy_health}", enemyHealth)
                .replace("{enemy_health_rounded}", enemyHealthRounded)
                .replace("{enemy_hearts}", enemyHearts);
    }
}

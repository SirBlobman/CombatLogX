package com.SirBlobman.combatlogx.expansion.notifier.hook;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;

import me.jasperjh.animatedscoreboard.AnimatedScoreboard;
import me.jasperjh.animatedscoreboard.config.PlayerScoreboardFile;
import me.jasperjh.animatedscoreboard.core.PlayerScoreboardHandler;
import me.jasperjh.animatedscoreboard.objects.ScoreboardPlayer;
import me.jasperjh.animatedscoreboard.objects.trigger.PlayerScoreboardTriggerTemplate;
import me.jasperjh.animatedscoreboard.objects.trigger.PlayerTriggerHandler;
import org.apache.commons.lang.Validate;

public final class HookAnimatedScoreboard {
    private static ScoreboardPlayer getPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        AnimatedScoreboard plugin = JavaPlugin.getPlugin(AnimatedScoreboard.class);

        PlayerScoreboardHandler scoreboardHandler = plugin.getScoreboardHandler();
        return scoreboardHandler.getPlayer(uuid);
    }

    public static void enable(Player player) {
        ScoreboardPlayer scoreboardPlayer = getPlayer(player);
        scoreboardPlayer.enableScoreboard();
    }

    public static void disable(Player player) {
        ScoreboardPlayer scoreboardPlayer = getPlayer(player);
        scoreboardPlayer.disableScoreboard();
    }
}
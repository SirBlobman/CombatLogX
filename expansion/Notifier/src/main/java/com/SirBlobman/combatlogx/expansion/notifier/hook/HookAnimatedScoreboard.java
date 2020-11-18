package com.SirBlobman.combatlogx.expansion.notifier.hook;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.jasperjh.animatedscoreboard.AnimatedScoreboard;
import me.jasperjh.animatedscoreboard.core.PlayerScoreboardHandler;
import me.jasperjh.animatedscoreboard.objects.ScoreboardPlayer;

public final class HookAnimatedScoreboard {
    private static final Set<UUID> scoreboardSet = new HashSet<>();
    private static ScoreboardPlayer getPlayer(Player player) {
        AnimatedScoreboard scoreboard = JavaPlugin.getPlugin(AnimatedScoreboard.class);
        PlayerScoreboardHandler scoreboardHandler = scoreboard.getScoreboardHandler();

        UUID uuid = player.getUniqueId();
        return scoreboardHandler.getPlayer(uuid);
    }

    public static void disable(Player player) {
        ScoreboardPlayer scoreboardPlayer = getPlayer(player);
        if(!scoreboardPlayer.hasScoreboard()) return;

        UUID uuid = player.getUniqueId();
        scoreboardPlayer.disableScoreboard();
        scoreboardSet.add(uuid);
    }

    public static void enable(Player player) {
        UUID uuid = player.getUniqueId();
        if(!scoreboardSet.contains(uuid)) return;

        ScoreboardPlayer scoreboardPlayer = getPlayer(player);
        scoreboardPlayer.enableScoreboard();
        scoreboardSet.remove(uuid);
    }
}
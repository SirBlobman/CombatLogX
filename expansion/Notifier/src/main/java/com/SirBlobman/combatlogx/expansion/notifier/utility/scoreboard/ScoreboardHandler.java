package com.SirBlobman.combatlogx.expansion.notifier.utility.scoreboard;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.SirBlobman.combatlogx.api.shaded.utility.Util;
import com.SirBlobman.combatlogx.expansion.notifier.Notifier;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class ScoreboardHandler {
    private static final Map<UUID, CustomScoreBoard> customScoreboardMap = Util.newMap();
    private static final Map<UUID, Scoreboard> previousScoreboardMap = Util.newMap();
    private static final List<UUID> noScoreboardList = Util.newList();

    public static void toggle(Player player) {
        if(player == null) return;

        UUID uuid = player.getUniqueId();
        if(noScoreboardList.contains(uuid)) {
            noScoreboardList.remove(uuid);
            return;
        }

        noScoreboardList.add(uuid);
    }

    public static boolean isDisabled(Player player) {
        if(player == null) return true;

        UUID uuid = player.getUniqueId();
        return noScoreboardList.contains(uuid);
    }

    public static void enableScoreboard(Notifier expansion, Player player) {
        if(expansion == null || player == null) return;
        UUID uuid = player.getUniqueId();

        FileConfiguration config = expansion.getConfig("scoreboard.yml");
        if(config.getBoolean("save-previous") && !previousScoreboardMap.containsKey(uuid)) {
            Scoreboard scoreboard = player.getScoreboard();
            if(scoreboard != null) {
                Objective objective = scoreboard.getObjective("combatlogx");
                if(objective == null) previousScoreboardMap.put(uuid, scoreboard);
            }
        }

        CustomScoreBoard custom = customScoreboardMap.getOrDefault(uuid, new CustomScoreBoard(expansion, player));
        custom.enableScoreboard();
        customScoreboardMap.put(uuid, custom);
    }

    public static void disableScoreboard(Notifier expansion, Player player) {
        if(expansion == null || player == null) return;
        UUID uuid = player.getUniqueId();

        CustomScoreBoard custom = customScoreboardMap.remove(uuid);
        if(custom != null) custom.disableScoreboard();

        FileConfiguration config = expansion.getConfig("scoreboard.yml");
        if(config.getBoolean("save-previous") && previousScoreboardMap.containsKey(uuid)) {
            Scoreboard scoreboard = previousScoreboardMap.remove(uuid);
            if(scoreboard != null) {
                Objective objective = scoreboard.getObjective("combatlogx");
                if(objective == null) player.setScoreboard(scoreboard);
            }
        }
    }

    public static void updateScoreboard(Notifier expansion, Player player) {
        if(expansion == null || player == null) return;
        UUID uuid = player.getUniqueId();

        CustomScoreBoard custom = customScoreboardMap.getOrDefault(uuid, null);
        if(custom == null) {
            enableScoreboard(expansion, player);
            custom = customScoreboardMap.get(uuid);
        }

        custom.enableScoreboard();
        custom.updateScoreboard();
    }
}
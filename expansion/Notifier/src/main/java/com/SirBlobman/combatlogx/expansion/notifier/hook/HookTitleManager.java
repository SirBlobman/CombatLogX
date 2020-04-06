package com.SirBlobman.combatlogx.expansion.notifier.hook;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.SirBlobman.combatlogx.api.shaded.utility.MessageUtil;
import com.SirBlobman.combatlogx.expansion.notifier.Notifier;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.puharesource.mc.titlemanager.TitleManagerPlugin;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import io.puharesource.mc.titlemanager.api.v2.animation.AnimationPart;

public final class HookTitleManager {
    private static TitleManagerAPI getAPI() {
        PluginManager manager = Bukkit.getPluginManager();
        if(!manager.isPluginEnabled("TitleManager")) return null;

        return (TitleManagerAPI) manager.getPlugin("TitleManager");
    }

    public static void disableScoreboard(Notifier expansion, Player player) {
        FileConfiguration config = expansion.getConfig("title-manager.yml");
        if(!config.getBoolean("disable-scoreboard")) return;

        TitleManagerAPI api = getAPI();
        if(api != null) api.removeScoreboard(player);
    }

    @SuppressWarnings("rawtypes")
    public static void restoreScoreboard(Notifier expansion, Player player) {
        FileConfiguration config = expansion.getConfig("title-manager.yml");
        if(!config.getBoolean("restore-scoreboard")) return;

        TitleManagerAPI api = getAPI();
        if(api == null) return;

        try {
            TitleManagerPlugin plugin = JavaPlugin.getPlugin(TitleManagerPlugin.class);

            FileConfiguration titleManagerConfig = plugin.getConfig();
            String scoreboardTitle = titleManagerConfig.getString("scoreboard.title");
            if(scoreboardTitle == null) scoreboardTitle = "";

            String scoreboardTitleColored = MessageUtil.color(scoreboardTitle);
            List<String> scoreboardLines = MessageUtil.colorList(titleManagerConfig.getStringList("scoreboard.lines"));
            List<String> validScoreboardLines = scoreboardLines.subList(0, Math.min(scoreboardLines.size(), 16));

            List<AnimationPart> titleAnimated = api.toAnimationParts(scoreboardTitleColored);
            List<List<AnimationPart>> linesAnimated = validScoreboardLines.stream().map(api::toAnimationParts).collect(Collectors.toList());

            api.giveScoreboard(player);
            api.toScoreboardTitleAnimation(titleAnimated, player, true).start();

            int linesAnimatedSize = linesAnimated.size();
            for(int i = 0; i < linesAnimatedSize; i++) {
                List<AnimationPart> lineAnimated = linesAnimated.get(i);
                int index = i + 1;
                api.toScoreboardValueAnimation(lineAnimated, player, index, true).start();
            }
        } catch(Exception ex) {
            Logger logger = expansion.getLogger();
            logger.log(Level.WARNING, "An error occurred while restoring the TitleManager scoreboard for '" + player.getName() + ".", ex);
        }
    }
}
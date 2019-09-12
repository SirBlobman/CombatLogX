package com.SirBlobman.expansion.notifier.hook;

import com.SirBlobman.combatlogx.utility.Util;
import io.puharesource.mc.titlemanager.TitleManagerPlugin;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import io.puharesource.mc.titlemanager.api.v2.animation.AnimationPart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public final class TitleManagerUtil {
    private static TitleManagerAPI getAPI() {
        PluginManager manager = Bukkit.getPluginManager();
        if(!manager.isPluginEnabled("TitleManager")) return null;

        return (TitleManagerAPI) manager.getPlugin("TitleManager");
    }

    public static void disableScoreboard(Player player) {
        TitleManagerAPI api = getAPI();
        if(api != null) api.removeScoreboard(player);
    }

    public static void restoreScoreboard(Player player) {
        Util.debug("Attempting to re-enable TitleManager Scoreboard...");

        TitleManagerAPI api = getAPI();
        if(api != null) {
            try {
                TitleManagerPlugin plugin = JavaPlugin.getPlugin(TitleManagerPlugin.class);

                List<AnimationPart> title = api.toAnimationParts(Util.color(plugin.getConfig().getString("scoreboard.title")));
                List<String> configLines = plugin.getConfig().getStringList("scoreboard.lines");
                List<List<AnimationPart>> lines = configLines.subList(0, Math.min(configLines.size(), 16)).stream().map(item -> api.toAnimationParts(Util.color(item))).collect(Collectors.toList());

                api.giveScoreboard(player);
                api.toScoreboardTitleAnimation(title, player, true).start();

                for(int i = 0; i < lines.size(); i++) {
                    List<AnimationPart> line = lines.get(i);
                    api.toScoreboardValueAnimation(line, player, i + 1, true).start();
                }
            } catch(Exception ex) {
                Util.log("An error occurred while restoring the TitleManager scoreboard for '" + player.getName() + "'.");
                ex.printStackTrace();
            }
        }
    }
}
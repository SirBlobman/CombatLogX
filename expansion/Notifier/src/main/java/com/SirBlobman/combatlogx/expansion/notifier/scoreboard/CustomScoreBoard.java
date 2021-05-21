package com.SirBlobman.combatlogx.expansion.notifier.scoreboard;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.shaded.nms.AbstractNMS;
import com.SirBlobman.combatlogx.api.shaded.nms.MultiVersionHandler;
import com.SirBlobman.combatlogx.api.shaded.nms.ScoreboardHandler;
import com.SirBlobman.combatlogx.api.shaded.nms.VersionUtil;
import com.SirBlobman.combatlogx.api.shaded.utility.MessageUtil;
import com.SirBlobman.combatlogx.api.shaded.utility.Util;
import com.SirBlobman.combatlogx.expansion.notifier.Notifier;
import com.SirBlobman.combatlogx.expansion.notifier.hook.HookMVdWPlaceholderAPI;
import com.SirBlobman.combatlogx.expansion.notifier.hook.HookPlaceholderAPI;
import com.SirBlobman.combatlogx.utility.PlaceholderReplacer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.*;

import org.apache.commons.lang.Validate;

public class CustomScoreBoard {
    private final Notifier expansion;
    private final UUID playerId;
    private final List<CustomLine> lineList = Util.newList();
    private final Scoreboard scoreboard;
    private Objective objective;
    public CustomScoreBoard(Notifier expansion, Player player) {
        this.expansion = Objects.requireNonNull(expansion, "expansion must not be null!");
        this.playerId = Objects.requireNonNull(player, "player must not be null!").getUniqueId();
    
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        this.scoreboard = Objects.requireNonNull(scoreboardManager, "Bukkit's Scoreboard Manager is not available yet!").getNewScoreboard();

        createObjective();
        initializeScoreboard();
    }

    private void createObjective() {
        FileConfiguration config = this.expansion.getConfig("scoreboard.yml");
        String scoreboardTitle = MessageUtil.color(config.getString("scoreboard-title"));
        
        ICombatLogX plugin = this.expansion.getPlugin();
        MultiVersionHandler<?> multiVersionHandler = plugin.getMultiVersionHandler();
        AbstractNMS nmsHandler = multiVersionHandler.getInterface();
        ScoreboardHandler scoreboardHandler = nmsHandler.getScoreboardHandler();
    
        this.objective = scoreboardHandler.createObjective(this.scoreboard, "combatlogx", "dummy", scoreboardTitle);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.playerId);
    }

    public void enableScoreboard() {
        Player player = getPlayer();
        if(player == null) return;

        player.setScoreboard(this.scoreboard);
    }

    public void disableScoreboard() {
        Player player = getPlayer();
        if(player == null) return;
    
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if(scoreboardManager == null) throw new IllegalStateException("The scoreboard manager is null!");
        
        Scoreboard mainScoreboard = scoreboardManager.getMainScoreboard();
        player.setScoreboard(mainScoreboard);
    }

    private void initializeScoreboard() {
        ChatColor[] colorArray = ChatColor.values();
        for(int i = 0; i < colorArray.length; i++) {
            ChatColor color = colorArray[i];
            Team team = this.scoreboard.registerNewTeam("line" + i);
            team.addEntry(color.toString());

            CustomLine line = new CustomLine(color, i, team);
            this.lineList.add(line);
        }
    }

    private CustomLine getLine(int line) {
        for(CustomLine custom : this.lineList) {
            int customLine = custom.getLine();
            if(customLine == line) return custom;
        }

        return null;
    }

    private void setLine(int line, String value) {
        CustomLine customLine = getLine(line);
        Validate.notNull(customLine, "Could not find scoreboard line with index '" + line + "'.");

        String scoreName = customLine.getColor().toString();
        Score score = this.objective.getScore(scoreName);
        score.setScore(line);

        int maxLength = getMaxPrefixOrSuffixLength();
        int valueLength = value.length();
        Team team = customLine.getTeam();

        if(valueLength <= maxLength) {
            team.setPrefix(value);
            team.setSuffix("");
            return;
        }

        String part1 = value.substring(0, maxLength);
        String part2 = value.substring(maxLength);
        if(part2.length() > maxLength) {
            part2 = part2.substring(0, maxLength);
        }

        team.setPrefix(part1);
        team.setSuffix(part2);
    }

    private void removeLine(int line) {
        CustomLine custom = getLine(line);
        Validate.notNull(custom, "Could not find scoreboard line with index '" + line + "'.");

        this.scoreboard.resetScores(custom.getColor().toString());
    }

    public void updateScoreboard() {
        List<String> scoreboardLineList = this.expansion.getConfig("scoreboard.yml").getStringList("scoreboard-lines");
        final int scoreboardLineListSize = scoreboardLineList.size();

        int index = 16;
        for(int i = 0; i < 16; i++) {
            if(i >= scoreboardLineListSize) {
                removeLine(index);
                continue;
            }

            String line = scoreboardLineList.get(i);
            line = MessageUtil.color(line);
            line = replacePlaceholders(line);
            setLine(index, line);
            index--;
        }
    }

    private String replacePlaceholders(String string) {
        ICombatLogX plugin = this.expansion.getPlugin();
        Player player = getPlayer();
        if(player == null) return string;
    
        PluginManager manager = Bukkit.getPluginManager();
        if(manager.isPluginEnabled("PlaceholderAPI")) string = HookPlaceholderAPI.replacePlaceholders(player, string);
        if(manager.isPluginEnabled("MVdWPlaceholderAPI")) string = HookMVdWPlaceholderAPI.replacePlaceholders(player, string);

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

    private int getMaxPrefixOrSuffixLength() {
        int minorVersion = VersionUtil.getMinorVersion();
        if(minorVersion >= 13) return 64;
        return 16;
    }
}

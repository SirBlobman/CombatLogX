package com.SirBlobman.combatlogx.expansion.notifier.utility.scoreboard;

import java.util.List;
import java.util.UUID;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.shaded.nms.NMS_Handler;
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
        Validate.notNull(player, "player must not be null!");

        this.expansion = expansion;
        this.playerId = player.getUniqueId();
    
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if(scoreboardManager == null) throw new IllegalStateException("The scoreboard manager is null!");
        this.scoreboard = scoreboardManager.getNewScoreboard();

        createObjective();
        initializeScoreboard();
    }

    private void createObjective() {
        FileConfiguration config = this.expansion.getConfig("scoreboard.yml");
        String scoreboardTitle = MessageUtil.color(config.getString("scoreboard-title"));
        this.objective = NMS_Handler.getHandler().getScoreboardHandler().createObjective(this.scoreboard, "combatlogx", "dummy", scoreboardTitle);
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
        return this.lineList.stream().filter(custom -> custom.getLine() == line).findFirst().orElse(null);
    }

    private void setLine(int line, String value) {
        CustomLine custom = getLine(line);
        Validate.notNull(custom, "Could not find scoreboard line with index '" + line + "'.");

        this.objective.getScore(custom.getColor().toString()).setScore(line);

        int maxLength = getMaxLineLength();
        int valueLength = value.length();
        if(valueLength > maxLength) {
            String part1 = value.substring(0, maxLength);
            String part2 = value.substring(maxLength);
            String colorCodes = ChatColor.getLastColors(part1);
            part2 = colorCodes + part2;
            if(part2.length() > maxLength) {
                part2 = part2.substring(0, maxLength);
            }

            custom.getTeam().setPrefix(part1);
            custom.getTeam().setSuffix(part2);
        } else custom.getTeam().setPrefix(value);
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

    private int getMaxLineLength() {
        int minorVersion = NMS_Handler.getMinorVersion();
        if(minorVersion <= 12) return 16;
        if(minorVersion <= 13) return 64;

        return 16;
    }
}
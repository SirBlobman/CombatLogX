package combatlogx.expansion.scoreboard.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.scoreboard.ScoreboardHandler;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

import combatlogx.expansion.scoreboard.ScoreboardExpansion;

public final class CustomScoreboard {
    private final ScoreboardExpansion expansion;
    private final List<CustomLine> customLineList;
    private final Scoreboard scoreboard;
    private final Player player;
    private Objective objective;

    public CustomScoreboard(ScoreboardExpansion expansion, Player player) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
        this.player = Validate.notNull(player, "player must not be null!");

        ScoreboardManager bukkitScoreboardManager = Bukkit.getScoreboardManager();
        if (bukkitScoreboardManager == null) {
            throw new IllegalStateException("The Bukkit scoreboard manager is not ready yet!");
        }

        this.scoreboard = bukkitScoreboardManager.getNewScoreboard();
        this.customLineList = new ArrayList<>();

        createObjective();
        initializeScoreboard();
    }

    private ScoreboardExpansion getExpansion() {
        return this.expansion;
    }

    private ICombatLogX getCombatLogX() {
        ScoreboardExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private LanguageManager getLanguageManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLanguageManager();
    }

    public Player getPlayer() {
        return this.player;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public void enableScoreboard() {
        Player player = getPlayer();
        Scoreboard scoreboard = getScoreboard();
        player.setScoreboard(scoreboard);
    }

    public void disableScoreboard() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            throw new IllegalStateException("The Bukkit scoreboard manager is not ready yet!");
        }

        Player player = getPlayer();
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard() {
        List<String> lineList = getLines();
        int lineListSize = lineList.size();

        for (int line = 16; line > 0; line--) {
            int index = (16 - line);
            if (index >= lineListSize) {
                removeLine(line);
                continue;
            }

            String value = lineList.get(index);
            setLine(line, value);
        }

        Player player = getPlayer();
        LanguageManager languageManager = getLanguageManager();
        String title = languageManager.getMessage(player, "expansion.scoreboard.title", null, true);
        String titleReplaced = replacePlaceholders(title);
        this.objective.setDisplayName(titleReplaced);
    }

    private void createObjective() {
        Player player = getPlayer();
        LanguageManager languageManager = getLanguageManager();
        String title = languageManager.getMessage(player, "expansion.scoreboard.title", null, true);
        String titleReplaced = replacePlaceholders(title);

        ICombatLogX plugin = this.expansion.getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        ScoreboardHandler scoreboardHandler = multiVersionHandler.getScoreboardHandler();

        Scoreboard scoreboard = getScoreboard();
        this.objective = scoreboardHandler.createObjective(scoreboard, "combatlogx", "dummy",
                titleReplaced);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private void initializeScoreboard() {
        Scoreboard scoreboard = getScoreboard();
        ChatColor[] chatColorArray = {
                ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED,
                ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLUE,
                ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW,
                ChatColor.WHITE
        };
        int chatColorArrayLength = chatColorArray.length;

        for (int i = 0; i < chatColorArrayLength; i++) {
            ChatColor chatColor = chatColorArray[i];
            String chatColorString = chatColor.toString();

            String teamName = ("line" + i);
            Team team = scoreboard.registerNewTeam(teamName);
            team.addEntry(chatColorString);

            CustomLine customLine = new CustomLine(chatColor, team, i + 1);
            this.customLineList.add(customLine);
        }
    }

    private CustomLine getLine(int line) {
        return this.customLineList.get(line - 1);
    }

    private void setLine(int line, String value) {
        CustomLine customLine = getLine(line);
        Validate.notNull(customLine, "Could not find scoreboard line '" + line + "'.");

        ChatColor chatColor = customLine.getChatColor();
        String chatColorString = chatColor.toString();
        Score score = this.objective.getScore(chatColorString);
        score.setScore(line);

        int lengthLimit = getLineLengthLimit();
        int valueLength = value.length();
        if (valueLength <= lengthLimit) {
            Team team = customLine.getTeam();
            team.setPrefix(value);
            team.setSuffix("");
            return;
        }

        String partOne = value.substring(0, lengthLimit);
        String partTwo = value.substring(lengthLimit);

        String partOneFinalColors = ChatColor.getLastColors(partOne);
        partTwo = (partOneFinalColors + partTwo);
        if (partTwo.length() > lengthLimit) {
            partTwo = partTwo.substring(0, lengthLimit);
        }

        Team team = customLine.getTeam();
        team.setPrefix(partOne);
        team.setSuffix(partTwo);
    }

    private void removeLine(int line) {
        CustomLine customLine = getLine(line);
        Validate.notNull(customLine, "Could not find scoreboard line '" + line + "'.");

        ChatColor chatColor = customLine.getChatColor();
        String chatColorString = chatColor.toString();
        Scoreboard scoreboard = getScoreboard();
        scoreboard.resetScores(chatColorString);
    }

    private int getLineLengthLimit() {
        int minorVersion = VersionUtility.getMinorVersion();
        return (minorVersion > 12 ? 64 : 16);
    }

    private List<String> getLines() {
        Player player = getPlayer();
        LanguageManager languageManager = getLanguageManager();
        String lines = languageManager.getMessage(player, "expansion.scoreboard.lines", null, true);

        String[] split = lines.split("\n");
        List<String> lineList = new ArrayList<>();

        for (String line : split) {
            String replaced = replacePlaceholders(line);
            lineList.add(replaced);
        }

        return lineList;
    }

    private String replacePlaceholders(String message) {
        ScoreboardExpansion expansion = getExpansion();
        ICombatLogX plugin = expansion.getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();

        Player player = getPlayer();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return message;
        }

        List<Entity> enemyList = tagInformation.getEnemies();
        IPlaceholderManager placeholderManager = plugin.getPlaceholderManager();
        return placeholderManager.replaceAll(player, enemyList, message);
    }
}

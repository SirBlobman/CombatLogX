package combatlogx.expansion.scoreboard.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.scoreboard.ScoreboardHandler;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;

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

        org.bukkit.scoreboard.ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if(scoreboardManager == null) throw new IllegalStateException("The Bukkit scoreboard manager is not ready yet!");
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.customLineList = new ArrayList<>();

        createObjective();
        initializeScoreboard();
    }

    public ScoreboardExpansion getExpansion() {
        return this.expansion;
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
        org.bukkit.scoreboard.ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if(scoreboardManager == null) throw new IllegalStateException("The Bukkit scoreboard manager is not ready yet!");

        Player player = getPlayer();
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard() {
        List<String> lineList = getLines();
        int lineListSize = lineList.size();

        for(int line = 16; line > 0; line--) {
            int index = (16 - line);
            if(index >= lineListSize) {
                removeLine(line);
                continue;
            }

            String value = lineList.get(index);
            setLine(line, value);
        }
    }

    private void createObjective() {
        ScoreboardExpansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        String title = configuration.getString("title");
        String titleColored = MessageUtility.color(title);

        ICombatLogX plugin = expansion.getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        ScoreboardHandler scoreboardHandler = multiVersionHandler.getScoreboardHandler();

        Scoreboard scoreboard = getScoreboard();
        this.objective = scoreboardHandler.createObjective(scoreboard, "combatlogx", "dummy", titleColored);
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

        for(int i = 0; i < chatColorArrayLength; i++) {
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
        if(valueLength <= lengthLimit) {
            Team team = customLine.getTeam();
            team.setPrefix(value);
            team.setSuffix("");
            return;
        }

        String partOne = value.substring(0, lengthLimit);
        String partTwo = value.substring(lengthLimit);

        String partOneFinalColors = ChatColor.getLastColors(partOne);
        partTwo = (partOneFinalColors + partTwo);
        if(partTwo.length() > lengthLimit) partTwo = partTwo.substring(0, lengthLimit);

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
        if(minorVersion > 12) return 64;
        return 16;
    }

    private List<String> getLines() {
        ScoreboardExpansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        List<String> lineList = configuration.getStringList("lines");
        return lineList.stream().map(this::replacePlaceholders).collect(Collectors.toList());
    }

    private String replacePlaceholders(String string) {
        ScoreboardExpansion expansion = getExpansion();
        ICombatLogX plugin = expansion.getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();

        Player player = getPlayer();
        LivingEntity enemy = combatManager.getEnemy(player);

        string = MessageUtility.color(string);
        return combatManager.replaceVariables(player, enemy, string);
    }
}

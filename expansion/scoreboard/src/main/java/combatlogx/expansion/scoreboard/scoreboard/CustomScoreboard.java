package combatlogx.expansion.scoreboard.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import com.SirBlobman.api.nms.MultiVersionHandler;
import com.SirBlobman.api.nms.scoreboard.ScoreboardHandler;
import com.SirBlobman.api.utility.MessageUtility;
import com.SirBlobman.api.utility.Validate;
import com.SirBlobman.api.utility.VersionUtility;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.ICombatManager;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.SirBlobman.combatlogx.api.utility.PlaceholderHelper;

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

        int line = 16;
        for(int i = 0; i < 16; i++) {
            if(i > lineListSize) {
                removeLine(i);
                continue;
            }

            String value = lineList.get(i);
            setLine(line--, value);
        }
    }

    private void createObjective() {
        ScoreboardExpansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
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
        ChatColor[] chatColorArray = ChatColor.values();
        int chatColorArrayLength = chatColorArray.length;

        for(int i = 0; i < chatColorArrayLength; i++) {
            ChatColor chatColor = chatColorArray[i];
            String chatColorString = chatColor.toString();

            String teamName = ("line" + i);
            Team team = scoreboard.registerNewTeam(teamName);
            team.addEntry(chatColorString);

            CustomLine customLine = new CustomLine(chatColor, team, i);
            this.customLineList.add(customLine);
        }
    }

    private CustomLine getLine(int line) {
        return this.customLineList.stream().filter(customLine -> customLine.getLine() == line).findFirst().orElse(null);
    }

    private void setLine(int line, String value) {
        CustomLine customLine = getLine(line);
        Validate.notNull(customLine, "Could not find scoreboard line '" + line + "'.");

        ChatColor chatColor = customLine.getChatColor();
        String chatColorString = chatColor.toString();
        Score score = objective.getScore(chatColorString);
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
        if(minorVersion >= 13) return 64;
        return 16;
    }

    private List<String> getLines() {
        ScoreboardExpansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getStringList("lines").stream().map(this::replacePlaceholders).collect(Collectors.toList());
    }

    private String replacePlaceholders(String string) {
        ScoreboardExpansion expansion = getExpansion();
        ICombatLogX plugin = expansion.getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();

        Player player = getPlayer();
        LivingEntity enemy = combatManager.getEnemy(player);

        string = MessageUtility.color(string);
        string = combatManager.replaceVariables(player, enemy, string);

        String timeLeft = PlaceholderHelper.getTimeLeft(plugin, player);
        String timeLeftDecimal = PlaceholderHelper.getTimeLeftDecimal(plugin, player);
        String inCombat = PlaceholderHelper.getInCombat(plugin, player);
        String combatStatus = PlaceholderHelper.getStatus(plugin, player);

        String enemyName = PlaceholderHelper.getEnemyName(plugin, player);
        String enemyHealth = PlaceholderHelper.getEnemyHealth(plugin, player);
        String enemyHearts = PlaceholderHelper.getEnemyHearts(plugin, player);
        String enemyHealthRounded = PlaceholderHelper.getEnemyHealthRounded(plugin, player);

        return string.replace("{time_left}", timeLeft).replace("{time_left_decimal}", timeLeftDecimal)
                .replace("{in_combat}", inCombat).replace("{status}", combatStatus)
                .replace("{enemy_name}", enemyName).replace("{enemy_health}", enemyHealth)
                .replace("{enemy_hearts}", enemyHearts)
                .replace("{enemy_health_rounded}", enemyHealthRounded);
    }
}
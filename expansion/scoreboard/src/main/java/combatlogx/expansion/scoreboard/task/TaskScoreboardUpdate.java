package combatlogx.expansion.scoreboard.task;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionConfigurationManager;

import combatlogx.expansion.scoreboard.ScoreboardExpansion;
import combatlogx.expansion.scoreboard.manager.ScoreboardManager;

public final class TaskScoreboardUpdate extends BukkitRunnable {
    private final ScoreboardExpansion expansion;
    public TaskScoreboardUpdate(ScoreboardExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    public void register() {
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        long period = configuration.getLong("update-period");
        JavaPlugin plugin = this.expansion.getPlugin().getPlugin();
        runTaskTimer(plugin, period, period);
    }

    @Override
    public void run() {
        ICombatLogX plugin = this.expansion.getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();
        ScoreboardManager scoreboardManager = this.expansion.getScoreboardManager();

        Collection<? extends Player> onlinePlayerCollection = Bukkit.getOnlinePlayers();
        for(Player player : onlinePlayerCollection) {
            if(combatManager.isInCombat(player)) scoreboardManager.updateScoreboard(player);
            else scoreboardManager.removeScoreboard(player);
        }
    }
}
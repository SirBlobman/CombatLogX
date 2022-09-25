package combatlogx.expansion.cheat.prevention.task;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

import combatlogx.expansion.cheat.prevention.CheatPreventionExpansion;

public final class FlightRetagTask extends BukkitRunnable {
    private final CheatPreventionExpansion expansion;

    public FlightRetagTask(CheatPreventionExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    @Override
    public void run() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("resources/flight.yml");
        if (!configuration.getBoolean("flight-retag", false)) {
            return;
        }

        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        List<Player> playerList = combatManager.getPlayersInCombat();
        for (Player player : playerList) {
            if (!player.isFlying()) {
                continue;
            }

            combatManager.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
        }
    }

    public void register() {
        JavaPlugin plugin = getPlugin();
        runTaskTimer(plugin, 10L, 10L);
    }

    private CheatPreventionExpansion getExpansion() {
        return this.expansion;
    }

    private ConfigurationManager getConfigurationManager() {
        CheatPreventionExpansion expansion = getExpansion();
        return expansion.getConfigurationManager();
    }

    private ICombatLogX getCombatLogX() {
        CheatPreventionExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private JavaPlugin getPlugin() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlugin();
    }
}

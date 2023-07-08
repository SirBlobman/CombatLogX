package combatlogx.expansion.cheat.prevention.task;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.details.TaskDetails;
import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IFlightConfiguration;

public final class FlightRetagTask extends TaskDetails {
    private final ICheatPreventionExpansion expansion;

    public FlightRetagTask(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion.getExpansion().getPlugin().getPlugin());
        this.expansion = expansion;
    }

    @Override
    public void run() {
        IFlightConfiguration flightConfiguration = getFlightConfiguration();
        if (flightConfiguration.isFlightRetag()) {
            run0();
        }
    }

    private void run0() {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        List<Player> playerList = combatManager.getPlayersInCombat();
        for (Player player : playerList) {
            if (player.isFlying()) {
                combatManager.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
            }
        }
    }

    public void register() {
        TaskScheduler scheduler = getCombatLogX().getFoliaHelper().getScheduler();
        setDelay(10L);
        setPeriod(10L);
        scheduler.scheduleTask(this);
    }

    private @NotNull ICheatPreventionExpansion getCheatPrevention() {
        return this.expansion;
    }

    private @NotNull IFlightConfiguration getFlightConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getFlightConfiguration();
    }

    private @NotNull Expansion getExpansion() {
        ICheatPreventionExpansion cheatPrevention = getCheatPrevention();
        return cheatPrevention.getExpansion();
    }

    private @NotNull ICombatLogX getCombatLogX() {
        Expansion expansion = getExpansion();
        return expansion.getPlugin();
    }
}

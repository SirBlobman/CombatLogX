package combatlogx.expansion.cheat.prevention.task;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IItemConfiguration;

public final class ElytraRetagTask extends BukkitRunnable {
    private final ICheatPreventionExpansion expansion;

    public ElytraRetagTask(@NotNull ICheatPreventionExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    @Override
    public void run() {
        IItemConfiguration itemConfiguration = getItemConfiguration();
        if (itemConfiguration.isElytraRetag()) {
            run0();
        }
    }

    private void run0() {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        List<Player> playerList = combatManager.getPlayersInCombat();
        for (Player player : playerList) {
            if (player.isGliding()) {
                combatManager.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
            }
        }
    }

    public void register() {
        JavaPlugin plugin = getPlugin();
        runTaskTimer(plugin, 10L, 10L);
    }

    private @NotNull ICheatPreventionExpansion getCheatPrevention() {
        return this.expansion;
    }

    private @NotNull IItemConfiguration getItemConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getItemConfiguration();
    }

    private @NotNull Expansion getExpansion() {
        ICheatPreventionExpansion cheatPrevention = getCheatPrevention();
        return cheatPrevention.getExpansion();
    }

    private @NotNull ICombatLogX getCombatLogX() {
        Expansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private @NotNull JavaPlugin getPlugin() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlugin();
    }
}

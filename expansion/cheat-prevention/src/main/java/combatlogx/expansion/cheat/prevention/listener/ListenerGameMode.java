package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IGameModeConfiguration;
import org.jetbrains.annotations.NotNull;

public final class ListenerGameMode extends CheatPreventionListener {
    public ListenerGameMode(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSwitch(PlayerGameModeChangeEvent e) {
        Player player = e.getPlayer();
        if (isInCombat(player)) {
            GameMode gameMode = e.getNewGameMode();
            GameMode forceMode = getForceSwitchMode();
            if (gameMode == forceMode) {
                return;
            }

            if (isPreventSwitching()) {
                e.setCancelled(true);
                sendMessage(player, "expansion.cheat-prevention.game-mode.no-switch");
                return;
            }

            checkUntag(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        if (isForceSwitch()) {
            Player player = e.getPlayer();
            GameMode gameMode = getForceSwitchMode();
            player.setGameMode(gameMode);

            String gameModeName = gameMode.name();
            Replacer replacer = new StringReplacer("{game_mode}", gameModeName);
            sendMessage(player, "expansion.cheat-prevention.game-mode.force-switch", replacer);
        }
    }

    private void checkUntag(@NotNull Player player) {
        if (isUntagOnSwitch()) {
            ICombatManager combatManager = getCombatManager();
            combatManager.untag(player, UntagReason.EXPIRE);
        }
    }

    private @NotNull IGameModeConfiguration getGameModeConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getGameModeConfiguration();
    }

    private boolean isPreventSwitching() {
        IGameModeConfiguration gameModeConfiguration = getGameModeConfiguration();
        return gameModeConfiguration.isPreventSwitching();
    }

    private boolean isForceSwitch() {
        IGameModeConfiguration gameModeConfiguration = getGameModeConfiguration();
        return gameModeConfiguration.isForceSwitch();
    }

    private boolean isUntagOnSwitch() {
        IGameModeConfiguration gameModeConfiguration = getGameModeConfiguration();
        return gameModeConfiguration.isUntagOnSwitch();
    }

    private @NotNull GameMode getForceSwitchMode() {
        IGameModeConfiguration gameModeConfiguration = getGameModeConfiguration();
        return gameModeConfiguration.getForceMode();
    }
}

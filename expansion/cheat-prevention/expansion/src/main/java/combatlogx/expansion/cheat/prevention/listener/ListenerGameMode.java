package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import com.SirBlobman.api.language.Replacer;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.ICombatManager;
import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.SirBlobman.combatlogx.api.object.UntagReason;

public final class ListenerGameMode extends CheatPreventionListener {
    public ListenerGameMode(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onSwitch(PlayerGameModeChangeEvent e) {
        Player player = e.getPlayer();
        if(!isInCombat(player)) return;
        if(isSwitchingAllowed()) {
            checkUntag(player);
            return;
        }

        GameMode gameMode = e.getNewGameMode();
        if(gameMode == getForceSwitchMode()) return;

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.game-mode.no-switch", null);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        if(!shouldForceSwitch()) return;
        GameMode gameMode = getForceSwitchMode();

        Player player = e.getPlayer();
        player.setGameMode(gameMode);

        String gameModeName = gameMode.name();
        Replacer replacer = message -> message.replace("{game_mode}", gameModeName);
        sendMessage(player, "expansion.cheat-prevention.game-mode.force-switch", replacer);
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("game-mode.yml");
    }

    private boolean isSwitchingAllowed() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-switching");
    }

    private boolean shouldForceSwitch() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("force-switch");
    }

    private boolean shouldUntagOnSwitch() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("untag-on-switch");
    }

    private GameMode getForceSwitchMode() {
        YamlConfiguration configuration = getConfiguration();
        String gameModeString = configuration.getString("force-mode");
        return parseGameMode(gameModeString);
    }

    private GameMode parseGameMode(String string) {
        try {
            String value = string.toUpperCase();
            return GameMode.valueOf(value);
        } catch(IllegalArgumentException | NullPointerException ex) {
            return GameMode.SURVIVAL;
        }
    }

    private void checkUntag(Player player) {
        if(!shouldUntagOnSwitch()) return;
        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        combatManager.untag(player, UntagReason.EXPIRE);
    }
}
package combatlogx.expansion.compatibility.citizens.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;

public final class ListenerQuit extends ExpansionListener {
    private final CitizensExpansion expansion;

    public ListenerQuit(CitizensExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e) {
        if(!isAlwaysSpawnOnQuit()) {
            return;
        }

        Player player = e.getPlayer();
        CombatNpcManager combatNpcManager = getCombatNpcManager();
        YamlConfiguration playerData = combatNpcManager.getData(player);

        printDebug("Spawning NPC for player " + player.getName());
        playerData.set("citizens-compatibility.punish", true);
        combatNpcManager.saveData(player);
        combatNpcManager.createNPC(player);
    }

    private CitizensExpansion getCitizensExpansion() {
        return this.expansion;
    }

    private CombatNpcManager getCombatNpcManager() {
        CitizensExpansion expansion = getCitizensExpansion();
        return expansion.getCombatNpcManager();
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("citizens.yml");
    }

    private boolean isAlwaysSpawnOnQuit() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("always-spawn-npc-on-quit", false);
    }
}

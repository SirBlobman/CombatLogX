package combatlogx.expansion.compatibility.citizens.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;

public final class ListenerPunish extends ExpansionListener {
    private final CitizensExpansion expansion;

    public ListenerPunish(CitizensExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforePunish(PlayerPunishEvent e) {
        printDebug("Detected PlayerPunishEvent.");

        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("citizens.yml");
        if (configuration.getBoolean("prevent-punishments")) {
            printDebug("Cancelling all other CombatLogX punishments.");
            e.setCancelled(true);
        }

        Player player = e.getPlayer();
        CombatNpcManager combatNpcManager = this.expansion.getCombatNpcManager();
        YamlConfiguration playerData = combatNpcManager.getData(player);

        printDebug("Spawning NPC for player " + player.getName());
        playerData.set("citizens-compatibility.punish", true);
        combatNpcManager.saveData(player);
        combatNpcManager.createNPC(player);
    }
}

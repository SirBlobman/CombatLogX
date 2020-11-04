package combatlogx.expansion.compatibility.citizens.listener;

import com.SirBlobman.combatlogx.api.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.SirBlobman.combatlogx.api.expansion.ExpansionListener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;

public final class ListenerPunish extends ExpansionListener {
    private final CitizensExpansion expansion;
    public ListenerPunish(CitizensExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforePunish(PlayerPunishEvent e) {
        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("citizens.yml");

        boolean cancel = configuration.getBoolean("prevent-punishments");
        if(cancel) e.setCancelled(true);

        Player player = e.getPlayer();
        CombatNpcManager combatNpcManager = this.expansion.getCombatNpcManager();
        YamlConfiguration data = combatNpcManager.getData(player);
        data.set("citizens-compatibility.punish", true);

        combatNpcManager.saveData(player);
        combatNpcManager.createNPC(player);
    }
}
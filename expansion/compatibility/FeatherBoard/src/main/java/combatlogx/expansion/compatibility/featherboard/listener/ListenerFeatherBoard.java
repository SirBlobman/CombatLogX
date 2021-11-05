package combatlogx.expansion.compatibility.featherboard.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import combatlogx.expansion.compatibility.featherboard.FeatherBoardExpansion;
import org.jetbrains.annotations.NotNull;

public final class ListenerFeatherBoard extends ExpansionListener {
    public ListenerFeatherBoard(FeatherBoardExpansion expansion) {
        super(expansion);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        showTrigger(player);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        removeTrigger(player);
    }
    
    @NotNull
    private String getTriggerName() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        String triggerName = configuration.getString("trigger-name");
        return (triggerName == null || triggerName.isEmpty() ? "combatlogx" : triggerName);
    }
    
    private void showTrigger(Player player) {
        String triggerName = getTriggerName();
        FeatherBoardAPI.showScoreboard(player, triggerName, true);
    }
    
    private void removeTrigger(Player player) {
        String triggerName = getTriggerName();
        FeatherBoardAPI.hideScoreboard(player, triggerName, true);
    }
}

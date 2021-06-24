package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class ListenerBuckets extends CheatPreventionListener {
    public ListenerBuckets(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        Player player = e.getPlayer();
        if(isInCombat(player) && shouldPreventEmpty()) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.buckets.no-empty", null);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBucketEmpty(PlayerBucketFillEvent e) {
        Player player = e.getPlayer();
        if(isInCombat(player) && shouldPreventFill()) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.buckets.no-fill", null);
        }
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("items.yml");
    }

    private boolean shouldPreventEmpty() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("prevent-bucket-empty", false);
    }

    private boolean shouldPreventFill() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("prevent-bucket-fill", false);
    }
}

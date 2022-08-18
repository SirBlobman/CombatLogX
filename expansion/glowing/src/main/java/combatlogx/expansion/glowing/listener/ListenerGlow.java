package combatlogx.expansion.glowing.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

public final class ListenerGlow extends ExpansionListener {
    public ListenerGlow(Expansion expansion) {
        super(expansion);
    }

    // Add glow effect when a player is tagged
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        player.setGlowing(true);
    }

    // Remove glow when a player is untagged.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        player.setGlowing(false);
    }

    // Remove glow when a player joins the server.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setGlowing(false);
    }

    // Remove glow when a player leaves the server
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        player.setGlowing(false);
    }

    // Remove glow when a player is kicked from the server
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        player.setGlowing(false);
    }

    // Remove glow when a player is killed.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        player.setGlowing(false);
    }

    // Remove glow when a player respawns
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        player.setGlowing(false);
    }
}

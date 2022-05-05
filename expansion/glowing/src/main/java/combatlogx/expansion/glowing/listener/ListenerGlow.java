package combatlogx.expansion.glowing.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

public final class ListenerGlow extends ExpansionListener {
    public ListenerGlow(Expansion expansion) {
        super(expansion);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        player.setGlowing(true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        player.setGlowing(false);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        player.setGlowing(false);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        player.setGlowing(false);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        player.setGlowing(false);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setGlowing(false);
    }
}

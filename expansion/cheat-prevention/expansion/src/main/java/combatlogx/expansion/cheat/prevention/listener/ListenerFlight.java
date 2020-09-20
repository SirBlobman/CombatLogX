package combatlogx.expansion.cheat.prevention.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;

public final class ListenerFlight extends CheatPreventionListener {
    private final Set<UUID> noFallDamageSet;
    public ListenerFlight(Expansion expansion) {
        super(expansion);
        this.noFallDamageSet = new HashSet<>();
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        checkFlight(player);
        checkAllowFlight(player);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onToggle(PlayerToggleFlightEvent e) {
        if(!e.isFlying()) return;
        if(isFlightAllowed()) return;

        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.flight.no-flying", null);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onDamage(EntityDamageEvent e) {
        DamageCause damageCause = e.getCause();
        if(damageCause != DamageCause.FALL) return;

        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        Player player = (Player) entity;

        UUID uuid = player.getUniqueId();
        if(shouldPreventFallDamageOnce() && this.noFallDamageSet.contains(uuid)) {
            this.noFallDamageSet.remove(uuid);
            e.setCancelled(true);
        }
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("flight.yml");
    }

    private boolean isFlightAllowed() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-flight");
    }

    private boolean shouldDisableAllowFlightFlag() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("force-disable-flight");
    }

    private boolean shouldPreventFallDamageOnce() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("prevent-fall-damage");
    }

    private void checkFlight(Player player) {
        if(!player.isFlying()) return;
        if(isFlightAllowed()) return;
        player.setFlying(false);

        if(shouldPreventFallDamageOnce()) {
            UUID uuid = player.getUniqueId();
            this.noFallDamageSet.add(uuid);
        }

        sendMessage(player, "expansion.cheat-prevention.flight.force-disabled", null);
    }

    private void checkAllowFlight(Player player) {
        if(!player.getAllowFlight()) return;
        if(!shouldDisableAllowFlightFlag()) return;

        player.setAllowFlight(false);
        sendMessage(player, "expansion.cheat-prevention.flight.force-disabled", null);
    }
}
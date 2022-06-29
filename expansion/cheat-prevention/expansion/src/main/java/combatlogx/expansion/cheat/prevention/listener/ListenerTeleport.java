package combatlogx.expansion.cheat.prevention.listener;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

public final class ListenerTeleport extends CheatPreventionListener {
    public ListenerTeleport(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if (!isInCombat(player)) return;

        checkPrevention(e);
        checkEnderPearlRetag(e);
        checkUntag(e);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent e) {
        Player player = e.getPlayer();
        if (!isInCombat(player)) return;

        YamlConfiguration configuration = getConfiguration();
        if (configuration.getBoolean("prevent-portals")) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.teleportation.block-portal", null);
        }
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("teleportation.yml");
    }

    private boolean isAllowed() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-teleportation");
    }

    private boolean isAllowed(TeleportCause teleportCause) {
        String teleportCauseName = teleportCause.name();
        YamlConfiguration configuration = getConfiguration();
        List<String> allowedTeleportCauseList = configuration.getStringList("allowed-teleport-cause-list");
        return allowedTeleportCauseList.contains(teleportCauseName);
    }

    private boolean shouldRetag() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("ender-pearl-retag");
    }

    private boolean shouldUntag() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("untag");
    }

    private void checkPrevention(PlayerTeleportEvent e) {
        printDebug("Checking if teleport event should be prevented...");
        if (e.isCancelled()) {
            printDebug("Event is already cancelled.");
            return;
        }

        if (isAllowed()) {
            printDebug("Teleportation is allowed by the config.");
            return;
        }

        TeleportCause teleportCause = e.getCause();
        if (isAllowed(teleportCause)) {
            printDebug("Teleportation cause '" + teleportCause + "' is allowed by the config.");
            return;
        }

        printDebug("Cancelling teleport event.");
        e.setCancelled(true);

        Player player = e.getPlayer();
        String messagePath = getMessagePath(teleportCause);
        sendMessage(player, messagePath, null);
    }

    private void checkEnderPearlRetag(PlayerTeleportEvent e) {
        printDebug("Checking if ender pearl should re-tag player...");
        if (!shouldRetag()) {
            printDebug("Re-tag option is disabled.");
            return;
        }

        if (e.isCancelled()) {
            printDebug("Event was cancelled, ignoring.");
            return;
        }

        TeleportCause teleportCause = e.getCause();
        if (teleportCause != TeleportCause.ENDER_PEARL) {
            printDebug("Teleport cause was not ENDER_PEARL, ignoring.");
            return;
        }

        Player player = e.getPlayer();
        ICombatManager combatManager = getCombatManager();
        LivingEntity enemy = combatManager.getEnemy(player);
        combatManager.tag(player, enemy, TagType.UNKNOWN, TagReason.UNKNOWN);
        printDebug("Player will be re-tagged. Done.");
    }

    private void checkUntag(PlayerTeleportEvent e) {
        printDebug("Checking if player should be untagged by teleport event...");
        if (!shouldUntag()) {
            printDebug("Untag option is set to false, not untagging.");
            return;
        }

        if (e.isCancelled()) {
            printDebug("Event was cancelled, not untagging.");
            return;
        }

        TeleportCause teleportCause = e.getCause();
        if (teleportCause == TeleportCause.UNKNOWN) {
            printDebug("Teleport cause was unknown, not untagging.");
            return;
        }

        Player player = e.getPlayer();
        ICombatManager combatManager = getCombatManager();
        combatManager.untag(player, UntagReason.EXPIRE);
        printDebug("Untagging player due to teleport event.");
    }

    private String getMessagePath(TeleportCause cause) {
        String mainPath = ("expansion.cheat-prevention.teleportation.block-");
        return (mainPath + (cause == TeleportCause.ENDER_PEARL ? "pearl" : "other"));
    }
}

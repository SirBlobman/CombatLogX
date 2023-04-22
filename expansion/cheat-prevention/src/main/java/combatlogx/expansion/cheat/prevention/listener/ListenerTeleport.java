package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.ITeleportConfiguration;
import org.jetbrains.annotations.NotNull;

public final class ListenerTeleport extends CheatPreventionListener {
    public ListenerTeleport(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if (isInCombat(player)) {
            checkPrevention(e);
            checkEnderPearlRetag(e);
            checkUntag(e);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent e) {
        Player player = e.getPlayer();
        if (isInCombat(player) && isPreventPortals()) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.teleportation.block-portal");
        }
    }

    private void checkPrevention(PlayerTeleportEvent e) {
        printDebug("Checking if teleport event should be prevented...");
        if (e.isCancelled()) {
            printDebug("Event is already cancelled.");
            return;
        }

        if (isPreventTeleportation()) {
            TeleportCause teleportCause = e.getCause();
            if (isAllowed(teleportCause)) {
                printDebug("Teleportation cause '" + teleportCause + "' is allowed by the config.");
                return;
            }

            printDebug("Cancelling teleport event.");
            e.setCancelled(true);

            Player player = e.getPlayer();
            String messagePath = getMessagePath(teleportCause);
            sendMessage(player, messagePath);
        }
    }

    private void checkEnderPearlRetag(PlayerTeleportEvent e) {
        printDebug("Checking if ender pearl should re-tag player...");
        if (e.isCancelled()) {
            printDebug("Event was cancelled, ignoring.");
            return;
        }

        if (isEnderPearlRetag() && e.getCause() == TeleportCause.ENDER_PEARL) {
            Player player = e.getPlayer();
            ICombatManager combatManager = getCombatManager();
            combatManager.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
            printDebug("Player will be re-tagged. Done.");
        }
    }

    private void checkUntag(PlayerTeleportEvent e) {
        printDebug("Checking if player should be untagged by teleport event...");
        if (e.isCancelled()) {
            printDebug("Event was cancelled, not untagging.");
            return;
        }

        if (isUntag() && e.getCause() != TeleportCause.UNKNOWN) {
            Player player = e.getPlayer();
            ICombatManager combatManager = getCombatManager();
            combatManager.untag(player, UntagReason.EXPIRE);
            printDebug("Untagging player due to teleport event.");
        }
    }

    private String getMessagePath(TeleportCause cause) {
        String mainPath = ("expansion.cheat-prevention.teleportation.block-");
        String causeName = (cause == TeleportCause.ENDER_PEARL ? "pearl" : "other");
        return (mainPath + causeName);
    }

    private @NotNull ITeleportConfiguration getTeleportConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getTeleportConfiguration();
    }

    private boolean isPreventTeleportation() {
        ITeleportConfiguration teleportConfiguration = getTeleportConfiguration();
        return teleportConfiguration.isPreventTeleportation();
    }

    private boolean isPreventPortals() {
        ITeleportConfiguration teleportConfiguration = getTeleportConfiguration();
        return teleportConfiguration.isPreventPortals();
    }

    private boolean isEnderPearlRetag() {
        ITeleportConfiguration teleportConfiguration = getTeleportConfiguration();
        return teleportConfiguration.isEnderPearlRetag();
    }

    private boolean isUntag() {
        ITeleportConfiguration teleportConfiguration = getTeleportConfiguration();
        return teleportConfiguration.isUntag();
    }

    private boolean isAllowed(@NotNull TeleportCause cause) {
        ITeleportConfiguration teleportConfiguration = getTeleportConfiguration();
        return teleportConfiguration.isAllowed(cause);
    }
}

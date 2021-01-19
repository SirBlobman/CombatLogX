package combatlogx.expansion.cheat.prevention.listener;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

public final class ListenerTeleport extends CheatPreventionListener {
    public ListenerTeleport(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        checkPrevention(e);
        checkEnderPearlRetag(e);
        checkUntag(e);
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
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
        TeleportCause teleportCause = e.getCause();
        if(e.isCancelled() || isAllowed() || isAllowed(teleportCause)) return;

        Player player = e.getPlayer();
        e.setCancelled(true);

        String messagePath = ("expansion.cheat-prevention.teleportation.block-" + (teleportCause == TeleportCause.ENDER_PEARL ? "pearl" : "other"));
        sendMessage(player, messagePath, null);
    }

    private void checkEnderPearlRetag(PlayerTeleportEvent e) {
        TeleportCause teleportCause = e.getCause();
        if(e.isCancelled() || teleportCause != TeleportCause.ENDER_PEARL || !shouldRetag()) return;

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        Player player = e.getPlayer();
        LivingEntity enemy = combatManager.getEnemy(player);
        combatManager.tag(player, enemy, TagType.UNKNOWN, TagReason.UNKNOWN);
    }

    private void checkUntag(PlayerTeleportEvent e) {
        if(e.isCancelled() || !shouldUntag()) return;
        Player player = e.getPlayer();

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        combatManager.untag(player, UntagReason.EXPIRE);
    }
}
package combatlogx.expansion.compatibility.essentials.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishExpansionListener;

import com.earth2me.essentials.CommandSource;
import combatlogx.expansion.compatibility.essentials.EssentialsExpansion;
import combatlogx.expansion.compatibility.essentials.EssentialsExpansionConfiguration;
import net.ess3.api.IUser;
import net.ess3.api.events.TPARequestEvent;

public final class ListenerEssentials extends VanishExpansionListener {
    private final EssentialsExpansion expansion;

    public ListenerEssentials(@NotNull EssentialsExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTeleportRequest(TPARequestEvent e) {
        if (!isPreventTeleportRequest()) {
            return;
        }

        CommandSource requester = e.getRequester();
        Player player = requester.getPlayer();
        if (player == null) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        if (isInCombat(player)) {
            e.setCancelled(true);
            String messagePath = "expansion.essentials-compatibility.prevent-teleport-request-self";
            languageManager.sendMessageWithPrefix(player, messagePath);
            return;
        }

        IUser targetUser = e.getTarget();
        Player target = targetUser.getBase();
        if (target == null) {
            return;
        }

        if (isInCombat(target)) {
            e.setCancelled(true);
            String messagePath = "expansion.essentials-compatibility.prevent-teleport-request-other";
            languageManager.sendMessageWithPrefix(player, messagePath);
        }
    }

    private @NotNull EssentialsExpansion getEssentialsExpansion() {
        return this.expansion;
    }

    private @NotNull EssentialsExpansionConfiguration getEssentialsConfiguration() {
        EssentialsExpansion expansion = getEssentialsExpansion();
        return expansion.getEssentialsConfiguration();
    }

    private boolean isPreventTeleportRequest() {
        EssentialsExpansionConfiguration configuration = getEssentialsConfiguration();
        return configuration.isPreventTeleportRequest();
    }
}

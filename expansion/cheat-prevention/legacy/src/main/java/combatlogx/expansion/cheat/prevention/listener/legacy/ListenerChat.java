package combatlogx.expansion.cheat.prevention.listener.legacy;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IChatConfiguration;
import combatlogx.expansion.cheat.prevention.listener.CheatPreventionListener;

public final class ListenerChat extends CheatPreventionListener {
    public ListenerChat(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        if (isChatDisabled()) {
            Player player = e.getPlayer();
            if (!isInCombat(player)) {
                return;
            }

            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.no-chat");
        }
    }

    private @NotNull IChatConfiguration getChatConfiguration() {
        ICheatPreventionExpansion cheatPrevention = getCheatPrevention();
        return cheatPrevention.getChatConfiguration();
    }

    private boolean isChatDisabled() {
        IChatConfiguration chatConfiguration = getChatConfiguration();
        return chatConfiguration.isDisableChat();
    }
}

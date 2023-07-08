package combatlogx.expansion.cheat.prevention.listener.paper;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import io.papermc.paper.event.player.AsyncChatEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IChatConfiguration;
import combatlogx.expansion.cheat.prevention.listener.CheatPreventionListener;

public final class ListenerPaperChat extends CheatPreventionListener {
    public ListenerPaperChat(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncChatEvent e) {
        Player player = e.getPlayer();
        if (isChatDisabled() && isInCombat(player)) {
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

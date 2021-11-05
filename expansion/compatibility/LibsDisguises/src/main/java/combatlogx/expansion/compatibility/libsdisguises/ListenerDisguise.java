package combatlogx.expansion.compatibility.libsdisguises;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import me.libraryaddict.disguise.DisguiseAPI;

public final class ListenerDisguise extends ExpansionListener {
    public ListenerDisguise(LibsDisguisesExpansion expansion) {
        super(expansion);
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        if(isDisguised(player)) {
            undisguise(player);
        }
    }
    
    private boolean isDisguised(Player player) {
        return DisguiseAPI.isDisguised(player);
    }
    
    private void undisguise(Player player) {
        DisguiseAPI.undisguiseToAll(player);
        sendMessageWithPrefix(player, "expansion.disguise-compatibility.remove-disguise",
                null, true);
    }
}

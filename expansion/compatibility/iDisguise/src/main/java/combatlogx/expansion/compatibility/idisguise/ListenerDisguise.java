package combatlogx.expansion.compatibility.idisguise;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.iDisguise;

public class ListenerDisguise extends ExpansionListener {
    public ListenerDisguise(DisguiseExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        if(isDisguised(player)) {
            undisguise(player);
        }
    }

    private DisguiseAPI getAPI() {
        iDisguise disguisePlugin = iDisguise.getInstance();
        return disguisePlugin.getAPI();
    }

    private boolean isDisguised(Player player) {
        DisguiseAPI api = getAPI();
        return api.isDisguised(player);
    }

    private void undisguise(Player player) {
        DisguiseAPI api = getAPI();
        api.undisguise(player, false);
    }
}

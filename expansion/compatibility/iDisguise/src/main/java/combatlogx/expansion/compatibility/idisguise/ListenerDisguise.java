package combatlogx.expansion.compatibility.idisguise;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.iDisguise;

public final class ListenerDisguise extends ExpansionListener {
    public ListenerDisguise(DisguiseExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        if (isDisguised(player)) {
            removeDisguise(player);
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

    private void removeDisguise(Player player) {
        DisguiseAPI api = getAPI();
        api.undisguise(player, false);
        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessageWithPrefix(player, "expansion.disguise-compatibility.remove-disguise");
    }
}

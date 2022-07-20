package combatlogx.expansion.compatibility.marriagemaster.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import at.pcgamingfreaks.MarriageMaster.Bukkit.API.Events.TPEvent;
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.Marriage;
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.MarriagePlayer;

public final class ListenerMarriageMaster extends ExpansionListener {
    public ListenerMarriageMaster(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTeleport(TPEvent e) {
        MarriagePlayer teleporter = e.getPlayer();
        Player bukkitTeleporter = teleporter.getPlayerOnline();
        if(bukkitTeleporter == null) {
            return;
        }

        Marriage marriageData = e.getMarriageData();
        MarriagePlayer partner = marriageData.getPartner(teleporter);
        if(partner != null) {
            Player bukkitPartner = partner.getPlayerOnline();
            if(bukkitPartner != null && isInCombat(bukkitPartner)) {
                e.setCancelled(true);
                String messagePath = ("expansion.marriagemaster-compatibility.prevent-teleport-partner");
                sendMessageWithPrefix(bukkitTeleporter, messagePath, null, true);
                return;
            }
        }

        if(isInCombat(bukkitTeleporter)) {
            e.setCancelled(true);
            String messagePath = ("expansion.marriagemaster-compatibility.prevent-teleport-self");
            sendMessageWithPrefix(bukkitTeleporter, messagePath, null, true);
        }
    }
}

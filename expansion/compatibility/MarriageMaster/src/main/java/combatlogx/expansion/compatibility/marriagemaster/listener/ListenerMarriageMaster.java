package combatlogx.expansion.compatibility.marriagemaster.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.language.LanguageManager;
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
        printDebug("Detected MarriageMaster TPEvent...");

        MarriagePlayer teleporter = e.getPlayer();
        Player bukkitTeleporter = teleporter.getPlayerOnline();
        if (bukkitTeleporter == null) {
            printDebug("Teleporter is null, ignoring.");
            return;
        }

        printDebug("Checking partner....");
        Marriage marriageData = e.getMarriageData();
        MarriagePlayer partner = marriageData.getPartner(teleporter);
        LanguageManager languageManager = getLanguageManager();

        if (partner != null) {
            Player bukkitPartner = partner.getPlayerOnline();
            if (bukkitPartner != null && isInCombat(bukkitPartner)) {
                printDebug("Partner is in combat, preventing teleport.");

                e.setCancelled(true);
                String messagePath = ("expansion.marriagemaster-compatibility.prevent-teleport-partner");
                languageManager.sendMessageWithPrefix(bukkitTeleporter, messagePath);
                return;
            }
        }

        printDebug("Partner was not in combat, checking teleporter...");
        if (isInCombat(bukkitTeleporter)) {
            printDebug("Teleporter is in combat, preventing teleport.");

            e.setCancelled(true);
            String messagePath = ("expansion.marriagemaster-compatibility.prevent-teleport-self");
            languageManager.sendMessageWithPrefix(bukkitTeleporter, messagePath);
        }
    }
}

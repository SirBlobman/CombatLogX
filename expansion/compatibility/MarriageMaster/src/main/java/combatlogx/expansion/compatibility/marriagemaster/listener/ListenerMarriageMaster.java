package combatlogx.expansion.compatibility.marriagemaster.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import at.pcgamingfreaks.MarriageMaster.Bukkit.API.Events.TPEvent;
import at.pcgamingfreaks.MarriageMaster.Bukkit.API.MarriagePlayer;

public final class ListenerMarriageMaster extends ExpansionListener {
    public ListenerMarriageMaster(Expansion expansion) {
        super(expansion);
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTeleport(TPEvent e) {
        MarriagePlayer marriagePlayer = e.getPlayer();
        Player bukkitPlayer = marriagePlayer.getPlayerOnline();
        if(bukkitPlayer == null) {
            return;
        }
        
        if(isInCombat(bukkitPlayer)) {
            e.setCancelled(true);
            String messagePath = ("expansion.marriagemaster-compatibility.prevent-teleport");
            sendMessageWithPrefix(bukkitPlayer, messagePath, null, true);
        }
    }
}

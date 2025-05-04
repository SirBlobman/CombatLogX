package combatlogx.expansion.compatibility.region.lands.listener;

import java.util.Collection;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import combatlogx.expansion.compatibility.region.lands.LandsExpansion;
import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;
import me.angeschossen.lands.api.events.war.WarDeclareEvent;
import me.angeschossen.lands.api.memberholder.MemberHolder;

public final class ListenerLands extends ExpansionListener {
    public ListenerLands(LandsExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWarDeclare(WarDeclareEvent e) {
        MemberHolder attacker = e.getAttacker();
        disableNewbieProtection(attacker);

        MemberHolder defender = e.getDefender();
        disableNewbieProtection(defender);
    }

    private void disableNewbieProtection(MemberHolder memberHolder) {
        Collection<Player> onlinePlayerCollection = memberHolder.getOnlinePlayers();
        for (Player player : onlinePlayerCollection) {
            disableNewbieProtection(player);
        }
    }

    private void disableNewbieProtection(Player player) {
        NewbieHelperExpansion newbieHelperExpansion = getNewbieHelperExpansion();
        ProtectionManager protectionManager = newbieHelperExpansion.getProtectionManager();
        PVPManager pvpManager = newbieHelperExpansion.getPVPManager();
        if (protectionManager.isProtected(player) || pvpManager.isDisabled(player)) {
            protectionManager.setProtected(player, false);
            pvpManager.setPVP(player, true);

            String path = ("expansion.region-protection.lands.war-disable-newbie-protection");
            LanguageManager languageManager = getLanguageManager();
            languageManager.sendMessageWithPrefix(player, path);
        }
    }

    private @NotNull NewbieHelperExpansion getNewbieHelperExpansion() {
        ICombatLogX combatLogX = getCombatLogX();
        ExpansionManager expansionManager = combatLogX.getExpansionManager();
        Optional<Expansion> optionalExpansion = expansionManager.getExpansion("NewbieHelper");
        if (optionalExpansion.isEmpty()) {
            throw new IllegalArgumentException("NewbieHelper expansion is missing.");
        }

        Expansion expansion = optionalExpansion.get();
        if (!(expansion instanceof NewbieHelperExpansion)) {
            throw new IllegalArgumentException("NewbieHelper expansion is not a proper instance.");
        }

        return (NewbieHelperExpansion) expansion;
    }
}

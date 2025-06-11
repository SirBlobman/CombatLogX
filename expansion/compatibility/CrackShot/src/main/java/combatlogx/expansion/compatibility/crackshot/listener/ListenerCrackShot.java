package combatlogx.expansion.compatibility.crackshot.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import combatlogx.expansion.compatibility.crackshot.CrackShotExpansion;

public final class ListenerCrackShot extends ExpansionListener {
    public ListenerCrackShot(@NotNull CrackShotExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(WeaponDamageEntityEvent e) {
        Entity entity = e.getVictim();
        if (!(entity instanceof Player damaged)) {
            return;
        }

        Player damager = e.getPlayer();
        ICombatManager combatManager = getCombatManager();
        combatManager.tag(damager, damaged, TagType.PLAYER, TagReason.ATTACKER);
        combatManager.tag(damaged, damager, TagType.PLAYER, TagReason.ATTACKED);
    }
}

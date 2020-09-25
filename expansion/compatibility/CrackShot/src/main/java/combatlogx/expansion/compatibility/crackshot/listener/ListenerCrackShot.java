package combatlogx.expansion.compatibility.crackshot.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.ICombatManager;
import com.SirBlobman.combatlogx.api.expansion.ExpansionListener;
import com.SirBlobman.combatlogx.api.object.TagReason;
import com.SirBlobman.combatlogx.api.object.TagType;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import combatlogx.expansion.compatibility.crackshot.CrackShotExpansion;

public class ListenerCrackShot extends ExpansionListener {
    public ListenerCrackShot(CrackShotExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onAttack(WeaponDamageEntityEvent e) {
        Entity entity = e.getVictim();
        if(!(entity instanceof Player)) return;
        Player damaged = (Player) entity;
        Player damager = e.getPlayer();

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        combatManager.tag(damager, damaged, TagType.PLAYER, TagReason.ATTACKER);
        combatManager.tag(damaged, damager, TagType.PLAYER, TagReason.ATTACKED);
    }
}
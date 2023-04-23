package com.github.sirblobman.combatlogx.listener;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
import com.github.sirblobman.combatlogx.api.listener.CombatListener;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.ICrystalManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

public final class ListenerEndCrystal extends CombatListener {
    public ListenerEndCrystal(@NotNull ICombatLogX plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        ICombatLogX plugin = getCombatLogX();
        MainConfiguration configuration = plugin.getConfiguration();
        if (!configuration.isLinkEndCrystals()) {
            return;
        }

        Entity damaged = e.getEntity();
        if (!(damaged instanceof Player)) {
            return;
        }

        Entity damager = e.getDamager();
        EntityType damagerType = damager.getType();
        if (damagerType != EntityType.ENDER_CRYSTAL) {
            return;
        }

        ICrystalManager crystalManager = plugin.getCrystalManager();
        Player placer = crystalManager.getPlacer(damager);
        Player player = (Player) damaged;

        if (placer != null) {
            checkTag(placer, player, TagReason.ATTACKER);
            checkTag(player, placer, TagReason.ATTACKED);
        }

        UUID damagerId = damager.getUniqueId();
        crystalManager.remove(damagerId);
    }

    private void checkTag(@NotNull Player player, @NotNull Player enemy, @NotNull TagReason tagReason) {
        ICombatManager combatManager = getCombatManager();
        combatManager.tag(player, enemy, TagType.PLAYER, tagReason);
    }
}

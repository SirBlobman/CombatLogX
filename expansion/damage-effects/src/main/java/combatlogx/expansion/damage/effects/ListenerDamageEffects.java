package combatlogx.expansion.damage.effects;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.utility.EntityHelper;

import combatlogx.expansion.damage.effects.effect.Blood;

public final class ListenerDamageEffects extends ExpansionListener {
    private final DamageEffectsExpansion expansion;

    public ListenerDamageEffects(@NotNull DamageEffectsExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        if (!(damaged instanceof Player)) {
            return;
        }

        ICombatLogX combatLogX = getCombatLogX();
        Entity damager = EntityHelper.linkProjectile(combatLogX, e.getDamager());
        DamageEffectsConfiguration configuration = getConfiguration();
        if (configuration.isAllDamage() || damager instanceof Player) {
            playBlood((Player) damaged);
        }
    }

    private @NotNull DamageEffectsExpansion getDamageEffectsExpansion() {
        return this.expansion;
    }

    private @NotNull DamageEffectsConfiguration getConfiguration() {
        DamageEffectsExpansion expansion = getDamageEffectsExpansion();
        return expansion.getConfiguration();
    }

    private void playBlood(@NotNull Player player) {
        DamageEffectsConfiguration configuration = getConfiguration();
        Blood blood = configuration.getBlood();
        blood.play(player);
    }
}

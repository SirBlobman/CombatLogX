package combatlogx.expansion.cheat.prevention.listener.modern;

import java.util.Collection;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IPotionConfiguration;
import combatlogx.expansion.cheat.prevention.listener.CheatPreventionListener;
import org.jetbrains.annotations.NotNull;

public class ListenerModernPotions extends CheatPreventionListener {
    public ListenerModernPotions(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        Collection<PotionEffect> activePotionEffectCollection = player.getActivePotionEffects();
        for (PotionEffect potionEffect : activePotionEffectCollection) {
            PotionEffectType potionEffectType = potionEffect.getType();
            if (isBlocked(potionEffectType)) {
                player.removePotionEffect(potionEffectType);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAddEffect(EntityPotionEffectEvent e) {
        Action action = e.getAction();
        if (action != Action.ADDED) {
            return;
        }

        EntityType entityType = e.getEntityType();
        if (entityType != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) e.getEntity();
        if (!isInCombat(player)) {
            return;
        }

        PotionEffectType potionEffectType = e.getModifiedType();
        if (isBlocked(potionEffectType)) {
            e.setCancelled(true);
        }
    }

    private @NotNull IPotionConfiguration getPotionConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getPotionConfiguration();
    }

    private boolean isBlocked(@NotNull PotionEffectType effectType) {
        IPotionConfiguration potionConfiguration = getPotionConfiguration();
        return potionConfiguration.isBlocked(effectType);
    }
}

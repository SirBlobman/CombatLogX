package combatlogx.expansion.damage.tagger.listener;

import java.util.Locale;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

import combatlogx.expansion.damage.tagger.DamageTaggerExpansion;
import combatlogx.expansion.damage.tagger.configuration.DamageTaggerConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ListenerDamage extends ExpansionListener {
    private final DamageTaggerExpansion expansion;

    public ListenerDamage(@NotNull DamageTaggerExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        DamageCause damageCause = e.getCause();

        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
            Entity damager = ee.getDamager();

            if (damageCause == DamageCause.ENTITY_EXPLOSION) {
                if (damager instanceof EnderCrystal && isCrystalDamageEnabled()) {
                    tag(player, damageCause);
                }
            }

            if (damageCause == DamageCause.FALLING_BLOCK) {
                tag(player, damageCause);
            }

            return;
        }

        if (isAllDamageEnabled()) {
            tag(player, null);
            return;
        }

        if (isEnabled(damageCause)) {
            tag(player, damageCause);
        }
    }

    private @NotNull DamageTaggerExpansion getDamageTagger() {
        return this.expansion;
    }

    private DamageTaggerConfiguration getConfiguration() {
        DamageTaggerExpansion expansion = getDamageTagger();
        return expansion.getConfiguration();
    }

    private boolean isAllDamageEnabled() {
        DamageTaggerConfiguration configuration = getConfiguration();
        return configuration.isAllDamage();
    }

    private boolean isCrystalDamageEnabled() {
        if (isAllDamageEnabled()) {
            return true;
        }

        DamageTaggerConfiguration configuration = getConfiguration();
        return configuration.isEndCrystals();
    }

    private boolean isEnabled(@NotNull DamageCause cause) {
        if (isAllDamageEnabled()) {
            return true;
        }

        DamageTaggerConfiguration configuration = getConfiguration();
        return configuration.isEnabled(cause);
    }

    private void tag(@NotNull Player player, @Nullable DamageCause damageCause) {
        ICombatManager combatManager = getCombatManager();
        DamageTaggerConfiguration configuration = getConfiguration();

        boolean alreadyInCombat = combatManager.isInCombat(player);
        boolean retagOnly = configuration.isRetagOnly();
        if (retagOnly && !alreadyInCombat) {
            return;
        }

        boolean tagged = combatManager.tag(player, null, TagType.DAMAGE, TagReason.UNKNOWN);
        if (tagged && !alreadyInCombat) {
            sendMessage(player, damageCause);
        }
    }

    private void sendMessage(@NotNull Player player, @Nullable DamageCause damageCause) {
        ICombatLogX combatLogX = getCombatLogX();
        LanguageManager languageManager = combatLogX.getLanguageManager();

        if (damageCause == null || isAllDamageEnabled()) {
            languageManager.sendMessageWithPrefix(player, "expansion.damage-tagger.unknown-damage");
            return;
        }

        String damageCauseName = damageCause.name();
        String damageCauseNameLowerCase = damageCauseName.toLowerCase(Locale.US);
        String damageCauseNameReplaced = damageCauseNameLowerCase.replace('_', '-');

        String messagePath = ("expansion.damage-tagger.damage-type." + damageCauseNameReplaced);
        languageManager.sendMessageWithPrefix(player, messagePath);
    }
}

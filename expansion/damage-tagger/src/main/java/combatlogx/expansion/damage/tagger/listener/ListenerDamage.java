package combatlogx.expansion.damage.tagger.listener;

import java.util.Locale;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

import combatlogx.expansion.damage.tagger.DamageTaggerExpansion;
import org.jetbrains.annotations.Nullable;

public final class ListenerDamage extends ExpansionListener {
    public ListenerDamage(DamageTaggerExpansion expansion) {
        super(expansion);
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
            if (damageCause == DamageCause.ENTITY_EXPLOSION) {
                Entity damager = ee.getDamager();
                if (damager instanceof EnderCrystal && isCrystalDamageEnabled()) {
                    tag(player, damageCause);
                }
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

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("config.yml");
    }

    private boolean isAllDamageEnabled() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("all-damage");
    }

    private boolean isCrystalDamageEnabled() {
        if (isAllDamageEnabled()) {
            return true;
        }

        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("end-crystals");
    }

    private boolean isEnabled(DamageCause damageCause) {
        if (isAllDamageEnabled()) {
            return true;
        }

        String damageCauseName = damageCause.name();
        String damageCauseNameLowerCase = damageCauseName.toLowerCase(Locale.US);
        String damageCauseNameReplaced = damageCauseNameLowerCase.replace('_', '-');

        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("damage-type." + damageCauseNameReplaced);
    }

    private void tag(Player player, @Nullable DamageCause damageCause) {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        YamlConfiguration configuration = getConfiguration();

        boolean alreadyInCombat = combatManager.isInCombat(player);
        boolean retagOnly = configuration.getBoolean("retag-only", false);
        if(retagOnly && !alreadyInCombat) {
            return;
        }

        boolean tagged = combatManager.tag(player, null, TagType.DAMAGE, TagReason.UNKNOWN);
        if (tagged && !alreadyInCombat) {
            sendMessage(player, damageCause);
        }
    }

    private void sendMessage(Player player, @Nullable DamageCause damageCause) {
        if (damageCause == null || isAllDamageEnabled()) {
            sendMessageWithPrefix(player, "expansion.damage-tagger.unknown-damage", null);
            return;
        }

        String damageCauseName = damageCause.name();
        String damageCauseNameLowerCase = damageCauseName.toLowerCase(Locale.US);
        String damageCauseNameReplaced = damageCauseNameLowerCase.replace('_', '-');

        String messagePath = ("expansion.damage-tagger.damage-type." + damageCauseNameReplaced);
        sendMessageWithPrefix(player, messagePath, null);
    }
}

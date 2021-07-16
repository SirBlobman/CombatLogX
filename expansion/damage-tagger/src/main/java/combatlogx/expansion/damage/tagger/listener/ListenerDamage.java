package combatlogx.expansion.damage.tagger.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.utility.EntityHelper;

import combatlogx.expansion.damage.tagger.DamageTaggerExpansion;

public final class ListenerDamage extends ExpansionListener {
    public ListenerDamage(DamageTaggerExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        if(checkDamageByEntity(e)) return;
        Player player = (Player) entity;

        DamageCause damageCause = e.getCause();
        if(isDisabled(damageCause)) return;

        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        boolean wasInCombat = combatManager.isInCombat(player);
        boolean tagged = combatManager.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
        if(tagged && !wasInCombat) sendMessage(player, damageCause);
    }

    private boolean checkDamageByEntity(EntityDamageEvent e) {
        if(!(e instanceof EntityDamageByEntityEvent)) return false;
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;

        Entity damager = EntityHelper.linkProjectile(getCombatLogX(), event.getDamager());
        return (damager instanceof LivingEntity);
    }

    private boolean isDisabled(DamageCause damageCause) {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if(configuration.getBoolean("all-damage")) return false;

        String damageCauseName = damageCause.name().toLowerCase();
        return !configuration.getBoolean("damage-type." + damageCauseName);
    }

    private void sendMessage(Player player, DamageCause damageCause) {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        LanguageManager languageManager = getLanguageManager();

        YamlConfiguration configuration = configurationManager.get("config.yml");
        if(configuration.getBoolean("all-damage")) {
            languageManager.sendMessage(player, "expansion.damage-tagger.unknown-damage", null, true);
            return;
        }

        String damageCauseName = damageCause.name().toLowerCase();
        String messagePath = ("expansion.damage-tagger.damage-type." + damageCauseName);
        languageManager.sendMessage(player, messagePath, null, true);
    }
}

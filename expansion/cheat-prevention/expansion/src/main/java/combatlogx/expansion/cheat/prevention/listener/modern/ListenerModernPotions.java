package combatlogx.expansion.cheat.prevention.listener.modern;

import java.util.Collection;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

public class ListenerModernPotions extends ExpansionListener {
    public ListenerModernPotions(final Expansion expansion) {
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

    @EventHandler
    public void onAddEffect(EntityPotionEffectEvent e) {
        if (e.getAction() != EntityPotionEffectEvent.Action.ADDED) return;
        if (e.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) e.getEntity();
        if (!isInCombat(player)) return;
        if (!isBlocked(e.getModifiedType())) return;
        e.setCancelled(true);
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("potions.yml");
    }

    private boolean isListInverted() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("blocked-potion-type-list-inverted", false);
    }

    private boolean isBlocked(PotionEffectType potionEffectType) {
        YamlConfiguration configuration = getConfiguration();
        List<String> potionEffectTypeNameList = configuration.getStringList("blocked-potion-type-list");
        String potionEffectTypeName = potionEffectType.getName();

        boolean inverted = isListInverted();
        boolean contains = potionEffectTypeNameList.contains(potionEffectTypeName);
        return (inverted != contains);
    }

}

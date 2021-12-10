package combatlogx.expansion.cheat.prevention.listener;

import java.util.Collection;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class ListenerPotions extends CheatPreventionListener {
    public ListenerPotions(Expansion expansion) {
        super(expansion);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        Collection<PotionEffect> activePotionEffectCollection = player.getActivePotionEffects();
        for(PotionEffect potionEffect : activePotionEffectCollection) {
            PotionEffectType potionEffectType = potionEffect.getType();
            if(isBlocked(potionEffectType)) {
                player.removePotionEffect(potionEffectType);
            }
        }
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

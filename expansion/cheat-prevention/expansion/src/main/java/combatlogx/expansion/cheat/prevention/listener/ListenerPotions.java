package combatlogx.expansion.cheat.prevention.listener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
        Set<PotionEffectType> removeList = getBlockedPotionEffectTypes();
        removeList.forEach(player::removePotionEffect);
    }
    
    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("potions.yml");
    }
    
    private Set<PotionEffectType> getBlockedPotionEffectTypes() {
        YamlConfiguration configuration = getConfiguration();
        List<String> potionEffectTypeNameList = configuration.getStringList("blocked-potion-type-list");
        Set<PotionEffectType> potionEffectTypeSet = new HashSet<>();
        
        for(String potionEffectTypeName : potionEffectTypeNameList) {
            PotionEffectType potionEffectType = PotionEffectType.getByName(potionEffectTypeName);
            if(potionEffectType == null) continue;
            potionEffectTypeSet.add(potionEffectType);
        }
        
        return potionEffectTypeSet;
    }
}

package combatlogx.expansion.cheat.prevention.listener.legacy;

import java.util.Collection;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import com.cryptomorin.xseries.XMaterial;
import combatlogx.expansion.cheat.prevention.listener.CheatPreventionListener;

public final class ListenerLegacyPotions extends CheatPreventionListener {
    public ListenerLegacyPotions(Expansion expansion) {
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
    public void onSplash(PotionSplashEvent e) {
        ThrownPotion thrownPotion = e.getPotion();
        Collection<PotionEffect> potionEffectCollection = thrownPotion.getEffects();

        Collection<LivingEntity> affectedEntityCollection = e.getAffectedEntities();
        for (LivingEntity affectedEntity : affectedEntityCollection) {
            if(!(affectedEntity instanceof Player)) {
                continue;
            }

            Player player = (Player) affectedEntity;
            if(!isInCombat(player)) {
                continue;
            }

            for (PotionEffect potionEffect : potionEffectCollection) {
                PotionEffectType potionEffectType = potionEffect.getType();
                if(!isBlocked(potionEffectType)) {
                    continue;
                }

                e.setIntensity(player, 0);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPotionConsume(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        if(!isInCombat(player)) {
            return;
        }

        ItemStack item = e.getItem();
        XMaterial material = XMaterial.matchXMaterial(item);
        if(material != XMaterial.POTION) {
            return;
        }

        Potion potion = Potion.fromItemStack(item);
        Collection<PotionEffect> potionEffectCollection = potion.getEffects();
        for(PotionEffect potionEffect : potionEffectCollection) {
            PotionEffectType potionEffectType = potionEffect.getType();
            if(isBlocked(potionEffectType)) {
                e.setCancelled(true);
                return;
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

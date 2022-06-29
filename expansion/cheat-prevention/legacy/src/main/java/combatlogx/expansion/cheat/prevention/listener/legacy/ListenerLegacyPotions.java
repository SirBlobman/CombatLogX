package combatlogx.expansion.cheat.prevention.listener.legacy;

import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

    @EventHandler
    public void onSplash(PotionSplashEvent e) {
        for (LivingEntity affectedEntity : e.getAffectedEntities()) {
            if (!(affectedEntity instanceof Player)) continue;
            Player player = (Player) affectedEntity;
            if (!isInCombat(player)) continue;
            for (final PotionEffect effect : e.getPotion().getEffects()) {
                if (!isBlocked(effect.getType())) continue;
                e.setIntensity(player, 0);
                break;
            }
        }
    }

    @EventHandler
    public void onPotionConsume(PlayerItemConsumeEvent e) {
        if (!isInCombat(e.getPlayer())) return;

        ItemStack stack = e.getItem();
        if (stack.getType() != Material.POTION) return;
        Potion potion = Potion.fromItemStack(stack);
        for (final PotionEffect effect : potion.getEffects()) {
            if (isBlocked(effect.getType())) {
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

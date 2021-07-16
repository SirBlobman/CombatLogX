package combatlogx.expansion.compatibility.mythicmobs;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public final class ListenerMythicMobs extends ExpansionListener {
    public ListenerMythicMobs(MythicMobsExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damager = e.getDamager();

        ICombatManager combatManager = getCombatManager();

        if(damaged instanceof Player && damager instanceof LivingEntity && isMythicMob(damager)) {
            String mobName = getMythicMobName(damager);
            if(isForceTag(mobName)) {
                Player playerDamaged = (Player) damaged;
                LivingEntity livingDamager = (LivingEntity) damager;
                combatManager.tag(playerDamaged, livingDamager, TagType.MOB, TagReason.ATTACKED);
            }
        }

        if(damager instanceof Player && damaged instanceof LivingEntity && isMythicMob(damaged)) {
            String mobName = getMythicMobName(damaged);
            if(isForceTag(mobName)) {
                Player playerDamager = (Player) damager;
                LivingEntity livingDamaged = (LivingEntity) damaged;
                combatManager.tag(playerDamager, livingDamaged, TagType.MOB, TagReason.ATTACKER);
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        LivingEntity enemy = e.getEnemy();
        if(enemy == null || !isMythicMob(enemy)) return;

        String mobName = getMythicMobName(enemy);
        if(isNoTag(mobName)) {
            e.setCancelled(true);
        }
    }

    private BukkitAPIHelper getAPI() {
        MythicMobs mythicMobs = MythicMobs.inst();
        return mythicMobs.getAPIHelper();
    }

    private boolean isMythicMob(Entity entity) {
        BukkitAPIHelper api = getAPI();
        return api.isMythicMob(entity);
    }

    private String getMythicMobName(Entity entity) {
        if(isMythicMob(entity)) {
            BukkitAPIHelper api = getAPI();
            ActiveMob activeMob = api.getMythicMobInstance(entity);
            return (activeMob == null ? null : activeMob.getMobType());
        }

        return null;
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("config.yml");
    }

    private boolean isForceTag(String mobName) {
        YamlConfiguration configuration = getConfiguration();
        List<String> forceTagMobTypeList = configuration.getStringList("force-tag-mob-type-list");
        return forceTagMobTypeList.contains(mobName);
    }

    private boolean isNoTag(String mobName) {
        YamlConfiguration configuration = getConfiguration();
        List<String> noTagMobTypeList = configuration.getStringList("no-tag-mob-type-list");
        return noTagMobTypeList.contains(mobName);
    }
}

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
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
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

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();

        if(damaged instanceof Player && damager instanceof LivingEntity && isMythicMob(damager)) {
            String mobName = getMythicMobName(damager);
            if(isForceTag(mobName)) {
                combatManager.tag((Player) damaged, (LivingEntity) damager, TagType.MOB, TagReason.ATTACKED);
            }
        }

        if(damager instanceof Player && isMythicMob(damaged)) {
            String mobName = getMythicMobName(damaged);
            if(isForceTag(mobName)) {
                combatManager.tag((Player) damager, (LivingEntity) damaged, TagType.MOB, TagReason.ATTACKER);
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

    private boolean isMythicMob(Entity entity) {
        MythicMobs mythicMobs = MythicMobs.inst();
        BukkitAPIHelper apiHelper = mythicMobs.getAPIHelper();
        return apiHelper.isMythicMob(entity);
    }

    private String getMythicMobName(Entity entity) {
        if(isMythicMob(entity)) {
            MythicMobs mythicMobs = MythicMobs.inst();
            BukkitAPIHelper apiHelper = mythicMobs.getAPIHelper();
            ActiveMob activeMob = apiHelper.getMythicMobInstance(entity);
            return (activeMob == null ? null : activeMob.getMobType());
        }

        return null;
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
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

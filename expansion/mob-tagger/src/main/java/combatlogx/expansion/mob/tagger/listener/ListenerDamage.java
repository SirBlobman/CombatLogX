package combatlogx.expansion.mob.tagger.listener;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.api.configuration.ConfigurationManager;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.ICombatManager;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.SirBlobman.combatlogx.api.expansion.ExpansionListener;
import com.SirBlobman.combatlogx.api.object.TagReason;
import com.SirBlobman.combatlogx.api.object.TagType;
import com.SirBlobman.combatlogx.api.utility.EntityHelper;

import combatlogx.expansion.mob.tagger.MobTaggerExpansion;

public final class ListenerDamage extends ExpansionListener {
    public ListenerDamage(MobTaggerExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        LivingEntity enemy = e.getEnemy();
        if(enemy == null || enemy instanceof Player) return;

        EntityType entityType = enemy.getType();
        if (isDisabled(entityType)) {
            e.setCancelled(true);
            return;
        }

        SpawnReason spawnReason = getSpawnReason(enemy);
        if(isDisabled(spawnReason)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damager = getDamager(e);
        checkTag(damaged, damager, TagReason.ATTACKED);
        checkTag(damager, damaged, TagReason.ATTACKER);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onSpawn(CreatureSpawnEvent e) {
        LivingEntity entity = e.getEntity();
        SpawnReason spawnReason = e.getSpawnReason();

        JavaPlugin plugin = getPlugin();
        MetadataValue metadataValue = new FixedMetadataValue(plugin, spawnReason);
        entity.setMetadata("spawn_reason", metadataValue);
    }

    private Entity getDamager(EntityDamageByEntityEvent e) {
        ICombatLogX plugin = getCombatLogX();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        Entity damager = e.getDamager();
        if(configuration.getBoolean("link-projectiles")) damager = EntityHelper.linkProjectile(damager);
        if(configuration.getBoolean("link-pets")) damager = EntityHelper.linkPet(damager);
        return damager;
    }

    private boolean isDisabled(EntityType entityType) {
        if(entityType == null || entityType == EntityType.PLAYER || !entityType.isAlive()) return true;
        String entityTypeName = entityType.name();

        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        List<String> mobList = configuration.getStringList("mob-list");
        return !mobList.contains(entityTypeName);
    }

    private boolean isDisabled(SpawnReason spawnReason) {
        if(spawnReason == null) return true;
        String spawnReasonName = spawnReason.name();

        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        List<String> spawnReasonList = configuration.getStringList("spawn-reason-list");
        return spawnReasonList.contains(spawnReasonName);
    }

    private void checkTag(Entity entity, Entity enemy, TagReason tagReason) {
        if(!(entity instanceof Player)) return;
        Player player = (Player) entity;

        if(!(enemy instanceof LivingEntity)) return;
        LivingEntity livingEnemy = (LivingEntity) enemy;

        EntityType enemyType = livingEnemy.getType();
        if(isDisabled(enemyType)) return;

        SpawnReason spawnReason = getSpawnReason(livingEnemy);
        if(isDisabled(spawnReason)) return;

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        combatManager.tag(player, livingEnemy, TagType.MOB, tagReason);
    }

    private SpawnReason getSpawnReason(LivingEntity entity) {
        if(entity == null || !entity.hasMetadata("spawn_reason")) return SpawnReason.DEFAULT;
        List<MetadataValue> metadataValueList = entity.getMetadata("spawn_reason");

        for(MetadataValue metadataValue : metadataValueList) {
            Object value = metadataValue.value();
            if(!(value instanceof SpawnReason)) continue;
            return (SpawnReason) value;
        }

        return SpawnReason.DEFAULT;
    }
}
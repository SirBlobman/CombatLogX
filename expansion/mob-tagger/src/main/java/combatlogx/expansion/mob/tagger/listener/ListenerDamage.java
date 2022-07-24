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
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.permissions.Permission;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.utility.EntityHelper;

import combatlogx.expansion.mob.tagger.MobTaggerExpansion;
import combatlogx.expansion.mob.tagger.manager.ISpawnReasonManager;

public final class ListenerDamage extends ExpansionListener {
    private final MobTaggerExpansion expansion;

    public ListenerDamage(MobTaggerExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    private MobTaggerExpansion getMobTaggerExpansion() {
        return this.expansion;
    }

    private ISpawnReasonManager getSpawnReasonManager() {
        MobTaggerExpansion expansion = getMobTaggerExpansion();
        return expansion.getSpawnReasonManager();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeTag(PlayerPreTagEvent e) {
        TagType tagType = e.getTagType();
        if (tagType != TagType.MOB) {
            return;
        }

        Entity enemy = e.getEnemy();
        if (enemy == null || enemy instanceof Player) {
            return;
        }

        EntityType entityType = enemy.getType();
        if (isDisabled(entityType)) {
            e.setCancelled(true);
            return;
        }

        SpawnReason spawnReason = getSpawnReason(enemy);
        if (isDisabled(spawnReason)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damager = getDamager(e);
        checkTag(damaged, damager, TagReason.ATTACKED);
        checkTag(damager, damaged, TagReason.ATTACKER);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent e) {
        ConfigurationManager configurationManager = getPluginConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("link-fishing-rod")) {
            return;
        }

        State state = e.getState();
        if (state != State.CAUGHT_ENTITY) {
            return;
        }

        Entity caughtEntity = e.getCaught();
        if (caughtEntity == null) {
            return;
        }

        Player player = e.getPlayer();
        checkTag(player, caughtEntity, TagReason.ATTACKER);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent e) {
        LivingEntity entity = e.getEntity();
        SpawnReason spawnReason = e.getSpawnReason();

        ISpawnReasonManager spawnReasonManager = getSpawnReasonManager();
        spawnReasonManager.setSpawnReason(entity, spawnReason);
    }

    private Entity getDamager(EntityDamageByEntityEvent e) {
        ICombatLogX plugin = getCombatLogX();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        Entity damager = e.getDamager();
        if (configuration.getBoolean("link-projectiles")) {
            damager = EntityHelper.linkProjectile(getCombatLogX(), damager);
        }

        if (configuration.getBoolean("link-pets")) {
            damager = EntityHelper.linkPet(damager);
        }

        if (configuration.getBoolean("link-tnt")) {
            damager = EntityHelper.linkTNT(damager);
        }

        return damager;
    }

    private boolean isDisabled(EntityType entityType) {
        if (entityType == null || entityType == EntityType.PLAYER || !entityType.isAlive()) {
            return true;
        }

        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        List<String> mobList = configuration.getStringList("mob-list");
        if (mobList.contains("*")) {
            return false;
        }

        String entityTypeName = entityType.name();
        return !mobList.contains(entityTypeName);
    }

    private boolean isDisabled(SpawnReason spawnReason) {
        if (spawnReason == null) {
            return true;
        }

        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        List<String> spawnReasonList = configuration.getStringList("spawn-reason-list");

        String spawnReasonName = spawnReason.name();
        return spawnReasonList.contains(spawnReasonName);
    }

    private void checkTag(Entity entity, Entity enemy, TagReason tagReason) {
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        if (hasBypassPermission(player)) {
            return;
        }

        if (!(enemy instanceof LivingEntity)) {
            return;
        }

        LivingEntity livingEnemy = (LivingEntity) enemy;
        EntityType enemyType = livingEnemy.getType();
        if (isDisabled(enemyType)) {
            return;
        }

        SpawnReason spawnReason = getSpawnReason(livingEnemy);
        if (isDisabled(spawnReason)) {
            return;
        }

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        combatManager.tag(player, livingEnemy, TagType.MOB, tagReason);
    }

    private SpawnReason getSpawnReason(Entity entity) {
        if (entity == null) {
            return SpawnReason.DEFAULT;
        }

        ISpawnReasonManager spawnReasonManager = getSpawnReasonManager();
        return spawnReasonManager.getSpawnReason(entity);
    }

    private boolean hasBypassPermission(Player player) {
        Permission bypassPermission = this.expansion.getMobCombatBypassPermission();
        if (bypassPermission == null) {
            return false;
        }

        return player.hasPermission(bypassPermission);
    }
}

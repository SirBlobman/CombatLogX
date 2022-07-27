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
        printDebug("Detected PlayerPreTagEvent...");

        TagType tagType = e.getTagType();
        if (tagType != TagType.MOB) {
            printDebug("TagType is not MOB, ignoring.");
            return;
        }

        Entity enemy = e.getEnemy();
        if (enemy == null || enemy instanceof Player) {
            printDebug("Enemy is null or player, ignoring.");
            return;
        }

        EntityType entityType = enemy.getType();
        if (isDisabled(entityType)) {
            printDebug("EntityType " + entityType + " is disabled, cancelling event.");
            e.setCancelled(true);
            return;
        }

        SpawnReason spawnReason = getSpawnReason(enemy);
        if (isDisabled(spawnReason)) {
            printDebug("SpawnReason " + spawnReason + " is disabled, cancelling event.");
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
        printDebug("Checking tag between entity " + entity + " and enemy " + enemy + " with reason "
                + tagReason + "...");
        if (!(entity instanceof Player)) {
            printDebug("entity is not player, ignoring.");
            return;
        }

        Player player = (Player) entity;
        if (hasBypassPermission(player)) {
            printDebug("Player has bypass permission, ignoring.");
            return;
        }

        EntityType enemyType = enemy.getType();
        if (isDisabled(enemyType)) {
            printDebug("Enemy type '" + enemyType + "' is disabled, ignoring.");
            return;
        }

        SpawnReason spawnReason = getSpawnReason(enemy);
        if (isDisabled(spawnReason)) {
            printDebug("Enemy spawn reason '" + spawnReason + "' is disabled, ignoring.");
            return;
        }

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        combatManager.tag(player, enemy, TagType.MOB, tagReason);
        printDebug("Tagged player with type MOB.");
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

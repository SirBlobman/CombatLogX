package combatlogx.expansion.mob.tagger.listener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.ICrystalManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.utility.EntityHelper;

import combatlogx.expansion.mob.tagger.MobTaggerExpansion;
import combatlogx.expansion.mob.tagger.configuration.MobTaggerConfiguration;
import combatlogx.expansion.mob.tagger.manager.ISpawnReasonManager;

public final class ListenerDamage extends ExpansionListener {
    private final MobTaggerExpansion expansion;

    public ListenerDamage(MobTaggerExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
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
        MobTaggerConfiguration configuration = getConfiguration();
        if (configuration.shouldNotTag(entityType)) {
            printDebug("EntityType " + entityType + " is disabled, cancelling event.");
            e.setCancelled(true);
            return;
        }

        SpawnReason spawnReason = getSpawnReason(enemy);
        if (configuration.shouldNotTag(spawnReason)) {
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
        ICombatLogX combatLogX = getCombatLogX();
        MainConfiguration configuration = combatLogX.getConfiguration();
        if (!configuration.isLinkFishingRod()) {
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
        if (spawnReasonManager != null) {
            spawnReasonManager.setSpawnReason(entity, spawnReason);
        }
    }

    private @NotNull MobTaggerExpansion getMobTaggerExpansion() {
        return this.expansion;
    }

    private @NotNull MobTaggerConfiguration getConfiguration() {
        MobTaggerExpansion expansion = getMobTaggerExpansion();
        return expansion.getConfiguration();
    }

    private @Nullable ISpawnReasonManager getSpawnReasonManager() {
        MobTaggerExpansion expansion = getMobTaggerExpansion();
        return expansion.getSpawnReasonManager();
    }

    private @NotNull Entity getDamager(@NotNull EntityDamageByEntityEvent e) {
        Entity entity = e.getDamager();
        return getDamager(entity);
    }

    private @NotNull Entity getDamager(@NotNull Entity entity) {
        ICombatLogX plugin = getCombatLogX();
        MainConfiguration configuration = plugin.getConfiguration();

        if (configuration.isLinkProjectiles()) {
            entity = EntityHelper.linkProjectile(plugin, entity);
        }

        if (configuration.isLinkPets()) {
            entity = EntityHelper.linkPet(entity);
        }

        if (configuration.isLinkTnt()) {
            entity = EntityHelper.linkTNT(entity);
        }

        if (configuration.isLinkEndCrystals()) {
            ICombatLogX combatLogX = getCombatLogX();
            ICrystalManager crystalManager = combatLogX.getCrystalManager();

            Player player = crystalManager.getPlacer(entity);
            if (player != null) {
                entity = player;
            }
        }

        return entity;
    }

    private boolean isDisabled(@NotNull SpawnReason spawnReason) {
        MobTaggerConfiguration configuration = getConfiguration();
        return configuration.shouldNotTag(spawnReason);
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
        MobTaggerConfiguration configuration = getConfiguration();
        if (configuration.shouldNotTag(enemyType)) {
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
        if (spawnReasonManager == null) {
            return SpawnReason.DEFAULT;
        }

        return spawnReasonManager.getSpawnReason(entity);
    }

    private boolean hasBypassPermission(Player player) {
        MobTaggerConfiguration configuration = getConfiguration();
        Permission bypassPermission = configuration.getBypassPermission();
        if (bypassPermission == null) {
            return false;
        }

        return player.hasPermission(bypassPermission);
    }
}

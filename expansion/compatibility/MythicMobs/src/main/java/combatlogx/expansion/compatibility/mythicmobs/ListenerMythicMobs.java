package combatlogx.expansion.compatibility.mythicmobs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;

public final class ListenerMythicMobs extends ExpansionListener {
    private final MythicMobsExpansion expansion;

    public ListenerMythicMobs(MythicMobsExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damager = e.getDamager();

        ICombatManager combatManager = getCombatManager();
        if (damaged instanceof Player && isMythicMob(damager)) {
            damager = linkMainMythicMob(damager);
            String mobName = getMythicMobName(damager);
            if (mobName != null && isForceTag(mobName)) {
                Player playerDamaged = (Player) damaged;
                combatManager.tag(playerDamaged, damager, TagType.MYTHIC_MOB, TagReason.ATTACKED);
            }
        }

        if (damager instanceof Player && isMythicMob(damaged)) {
            damaged = linkMainMythicMob(damaged);
            String mobName = getMythicMobName(damaged);
            if (mobName != null && isForceTag(mobName)) {
                Player playerDamager = (Player) damager;
                combatManager.tag(playerDamager, damaged, TagType.MYTHIC_MOB, TagReason.ATTACKER);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void beforeTag(PlayerPreTagEvent e) {
        TagType tagType = e.getTagType();
        if (tagType != TagType.MYTHIC_MOB) {
            return;
        }

        Entity enemy = e.getEnemy();
        if (enemy == null || !isMythicMob(enemy)) {
            return;
        }

        String mobName = getMythicMobName(enemy);
        if (mobName != null && isNoTag(mobName)) {
            e.setCancelled(true);
        }
    }

    private @NotNull BukkitAPIHelper getAPI() {
        MythicBukkit mythicBukkit = MythicBukkit.inst();
        return mythicBukkit.getAPIHelper();
    }

    private boolean isMythicMob(@NotNull Entity entity) {
        BukkitAPIHelper api = getAPI();
        return api.isMythicMob(entity);
    }

    private @Nullable ActiveMob getActiveMob(@NotNull Entity entity) {
        BukkitAPIHelper api = getAPI();
        return api.getMythicMobInstance(entity);
    }

    private @Nullable String getMythicMobName(@NotNull Entity entity) {
        if (isMythicMob(entity)) {
            ActiveMob activeMob = getActiveMob(entity);
            return (activeMob == null ? null : activeMob.getMobType());
        }

        return null;
    }

    private @NotNull Entity linkMainMythicMob(@NotNull Entity original) {
        ActiveMob activeMob = getActiveMob(original);
        if (activeMob == null) {
            return original;
        }

        AbstractEntity entity = activeMob.getEntity();
        if (entity == null) {
            return original;
        }

        return entity.getBukkitEntity();
    }

    private @NotNull MythicMobsExpansion getMythicMobsExpansion() {
        return this.expansion;
    }

    private @NotNull MythicMobsConfiguration getConfiguration() {
        MythicMobsExpansion expansion = getMythicMobsExpansion();
        return expansion.getConfiguration();
    }

    private boolean isForceTag(@NotNull String mobName) {
        MythicMobsConfiguration configuration = getConfiguration();
        return configuration.isForceTag(mobName);
    }

    private boolean isNoTag(@NotNull String mobName) {
        MythicMobsConfiguration configuration = getConfiguration();
        return configuration.isNoTag(mobName);
    }
}

package combatlogx.expansion.newbie.helper.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.ICrystalManager;
import com.github.sirblobman.combatlogx.api.utility.EntityHelper;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.configuration.NewbieHelperConfiguration;
import combatlogx.expansion.newbie.helper.configuration.WorldsConfiguration;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;

public final class ListenerDamage extends ExpansionListener {
    private final NewbieHelperExpansion expansion;

    public ListenerDamage(@NotNull final NewbieHelperExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamageByMob(final EntityDamageByEntityEvent e) {
        final Entity damaged = e.getEntity();
        if (!(damaged instanceof final Player player)) {
            return;
        }

        if (isWorldDisabled(player)) {
            return;
        }

        final Entity damager = getDamager(e);
        if (damager instanceof Player) {
            return;
        }

        final ProtectionManager protectionManager = this.expansion.getProtectionManager();
        if (protectionManager.isProtected(player) && isMobProtection()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamageMob(final EntityDamageByEntityEvent e) {
        final Entity damaged = e.getEntity();
        if (damaged instanceof Player) {
            return;
        }

        final Entity damager = getDamager(e);
        if (!(damager instanceof final Player player)) {
            return;
        }

        if (isWorldDisabled(player)) {
            return;
        }

        final ProtectionManager protectionManager = this.expansion.getProtectionManager();
        if (protectionManager.isProtected(player) && isMobProtection()) {
            if (isRemoveProtectionOnAttack()) {
                protectionManager.setProtected(player, false);
                final String messagePath = ("expansion.newbie-helper.protection-disabled.attacker");
                final LanguageManager languageManager = getLanguageManager();
                languageManager.sendMessageWithPrefix(player, messagePath);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamageByPlayer(final EntityDamageByEntityEvent e) {
        final Entity damaged = e.getEntity();
        if (!(damaged instanceof final Player attacked)) {
            return;
        }

        if (isWorldDisabled(attacked)) {
            return;
        }

        final Entity damager = getDamager(e);
        if (!(damager instanceof final Player attacker)) {
            return;
        }

        if (isWorldDisabled(attacker)) {
            return;
        }

        if (isForcePvpWorld(damager)) {
            return;
        }

        if (isNoPvpWorld(damager)) {
            e.setCancelled(true);
            return;
        }

        final NewbieHelperExpansion expansion = getNewbieHelperExpansion();
        final ProtectionManager protectionManager = expansion.getProtectionManager();
        final PVPManager pvpManager = expansion.getPVPManager();
        final LanguageManager languageManager = getLanguageManager();

        if (pvpManager.isDisabled(attacked)) {
            e.setCancelled(true);
            languageManager.sendMessageWithPrefix(attacker, "expansion.newbie-helper.no-pvp.other");
            return;
        }

        if (pvpManager.isDisabled(attacker)) {
            e.setCancelled(true);
            languageManager.sendMessageWithPrefix(attacker, "expansion.newbie-helper.no-pvp.self");
            return;
        }

        if (protectionManager.isProtected(attacked)) {
            e.setCancelled(true);
            final String messagePath = ("expansion.newbie-helper.no-pvp.protected");
            languageManager.sendMessageWithPrefix(attacker, messagePath);
            return;
        }

        if (protectionManager.isProtected(attacker)) {
            if (isRemoveProtectionOnAttack()) {
                protectionManager.setProtected(attacker, false);
                final String messagePath = ("expansion.newbie-helper.protection-disabled.attacker");
                languageManager.sendMessageWithPrefix(attacker, messagePath);
                return;
            }
            if (!isNewPlayerCauseDamage()) {
                e.setCancelled(true);
                final String messagePath = ("expansion.newbie-helper.no-pvp.cancel");
                languageManager.sendMessageWithPrefix(attacker, messagePath);
            }
        }
    }

    private @NotNull NewbieHelperExpansion getNewbieHelperExpansion() {
        return this.expansion;
    }

    private @NotNull NewbieHelperConfiguration getConfiguration() {
        final NewbieHelperExpansion expansion = getNewbieHelperExpansion();
        return expansion.getConfiguration();
    }

    private @NotNull WorldsConfiguration getWorldsConfiguration() {
        final NewbieHelperExpansion expansion = getNewbieHelperExpansion();
        return expansion.getWorldsConfiguration();
    }

    private boolean isForcePvpWorld(@NotNull final Entity entity) {
        final World world = entity.getWorld();
        return isForcePvpWorld(world);
    }

    private boolean isForcePvpWorld(@NotNull final World world) {
        final WorldsConfiguration configuration = getWorldsConfiguration();
        return configuration.isForcePvp(world);
    }

    private boolean isNoPvpWorld(@NotNull final Entity entity) {
        final World world = entity.getWorld();
        return isNoPvpWorld(world);
    }

    private boolean isNoPvpWorld(@NotNull final World world) {
        final WorldsConfiguration configuration = getWorldsConfiguration();
        return configuration.isNoPvp(world);
    }

    private boolean isRemoveProtectionOnAttack() {
        final NewbieHelperConfiguration configuration = getConfiguration();
        return configuration.isRemoveProtectionOnAttack();
    }

    private boolean isNewPlayerCauseDamage() {
        final NewbieHelperConfiguration configuration = getConfiguration();
        return configuration.isNewPlayerCauseDamage();
    }

    private boolean isMobProtection() {
        final NewbieHelperConfiguration configuration = getConfiguration();
        return configuration.isMobProtection();
    }

    private @NotNull Entity getDamager(@NotNull final EntityDamageByEntityEvent e) {
        final Entity entity = e.getDamager();
        return getDamager(entity);
    }

    private @NotNull Entity getDamager(@NotNull Entity entity) {
        final ICombatLogX plugin = getCombatLogX();
        final MainConfiguration configuration = plugin.getConfiguration();

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
            final ICombatLogX combatLogX = getCombatLogX();
            final ICrystalManager crystalManager = combatLogX.getCrystalManager();

            final Player player = crystalManager.getPlacer(entity);
            if (player != null) {
                entity = player;
            }
        }

        return entity;
    }
}

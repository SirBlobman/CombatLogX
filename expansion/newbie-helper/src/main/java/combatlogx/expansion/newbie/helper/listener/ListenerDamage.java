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

    public ListenerDamage(@NotNull NewbieHelperExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamageByMob(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        if (!(damaged instanceof Player)) {
            return;
        }

        Player player = (Player) damaged;
        if (isWorldDisabled(player)) {
            return;
        }

        Entity damager = getDamager(e);
        if (damager instanceof Player) {
            return;
        }

        ProtectionManager protectionManager = this.expansion.getProtectionManager();
        if (protectionManager.isProtected(player) && isMobProtection()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamageMob(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        if (damaged instanceof Player) {
            return;
        }

        Entity damager = getDamager(e);
        if (!(damager instanceof Player)) {
            return;
        }

        Player player = (Player) damager;
        if (isWorldDisabled(player)) {
            return;
        }

        ProtectionManager protectionManager = this.expansion.getProtectionManager();
        if (protectionManager.isProtected(player) && isMobProtection()) {
            if (isRemoveProtectionOnAttack()) {
                protectionManager.setProtected(player, false);
                String messagePath = ("expansion.newbie-helper.protection-disabled.attacker");
                LanguageManager languageManager = getLanguageManager();
                languageManager.sendMessageWithPrefix(player, messagePath);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamageByPlayer(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        if (!(damaged instanceof Player)) {
            return;
        }

        Player attacked = (Player) damaged;
        if (isWorldDisabled(attacked)) {
            return;
        }

        Entity damager = getDamager(e);
        if (!(damager instanceof Player)) {
            return;
        }

        Player attacker = (Player) damager;
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

        NewbieHelperExpansion expansion = getNewbieHelperExpansion();
        ProtectionManager protectionManager = expansion.getProtectionManager();
        PVPManager pvpManager = expansion.getPVPManager();
        LanguageManager languageManager = getLanguageManager();

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
            String messagePath = ("expansion.newbie-helper.no-pvp.protected");
            languageManager.sendMessageWithPrefix(attacker, messagePath);
            return;
        }

        if (protectionManager.isProtected(attacker)) {
            if (isRemoveProtectionOnAttack()) {
                protectionManager.setProtected(attacker, false);
                String messagePath = ("expansion.newbie-helper.protection-disabled.attacker");
                languageManager.sendMessageWithPrefix(attacker, messagePath);
            }
        }
    }

    private @NotNull NewbieHelperExpansion getNewbieHelperExpansion() {
        return this.expansion;
    }

    private @NotNull NewbieHelperConfiguration getConfiguration() {
        NewbieHelperExpansion expansion = getNewbieHelperExpansion();
        return expansion.getConfiguration();
    }

    private @NotNull WorldsConfiguration getWorldsConfiguration() {
        NewbieHelperExpansion expansion = getNewbieHelperExpansion();
        return expansion.getWorldsConfiguration();
    }

    private boolean isForcePvpWorld(@NotNull Entity entity) {
        World world = entity.getWorld();
        return isForcePvpWorld(world);
    }

    private boolean isForcePvpWorld(@NotNull World world) {
        WorldsConfiguration configuration = getWorldsConfiguration();
        return configuration.isForcePvp(world);
    }

    private boolean isNoPvpWorld(@NotNull Entity entity) {
        World world = entity.getWorld();
        return isNoPvpWorld(world);
    }

    private boolean isNoPvpWorld(@NotNull World world) {
        WorldsConfiguration configuration = getWorldsConfiguration();
        return configuration.isNoPvp(world);
    }

    private boolean isRemoveProtectionOnAttack() {
        NewbieHelperConfiguration configuration = getConfiguration();
        return configuration.isRemoveProtectionOnAttack();
    }

    private boolean isMobProtection() {
        NewbieHelperConfiguration configuration = getConfiguration();
        return configuration.isMobProtection();
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
}

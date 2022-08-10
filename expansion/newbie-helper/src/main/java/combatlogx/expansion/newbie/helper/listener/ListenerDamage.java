package combatlogx.expansion.newbie.helper.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.utility.EntityHelper;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;

public final class ListenerDamage extends ExpansionListener {
    private final NewbieHelperExpansion expansion;

    public ListenerDamage(NewbieHelperExpansion expansion) {
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
        if (protectionManager.isProtected(player) && isMobProtectionEnabled()) {
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
        if (protectionManager.isProtected(player) && isMobProtectionEnabled()) {
            if (shouldRemoveProtectionOnAttack()) {
                protectionManager.setProtected(player, false);
                String messagePath = ("expansion.newbie-helper.protection-disabled.attacker");
                sendMessageWithPrefix(player, messagePath, null);
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

        NewbieHelperExpansion expansion = getNewbieHelperExpansion();
        ProtectionManager protectionManager = expansion.getProtectionManager();
        PVPManager pvpManager = expansion.getPVPManager();

        if (pvpManager.isDisabled(attacked)) {
            e.setCancelled(true);
            sendMessageWithPrefix(attacker, "expansion.newbie-helper.no-pvp.other", null);
            return;
        }

        if (pvpManager.isDisabled(attacker)) {
            e.setCancelled(true);
            sendMessageWithPrefix(attacker, "expansion.newbie-helper.no-pvp.self", null);
            return;
        }

        if (protectionManager.isProtected(attacked)) {
            e.setCancelled(true);
            String messagePath = ("expansion.newbie-helper.no-pvp.protected");
            sendMessageWithPrefix(attacker, messagePath, null);
            return;
        }

        if (protectionManager.isProtected(attacker)) {
            if (shouldRemoveProtectionOnAttack()) {
                protectionManager.setProtected(attacker, false);
                String messagePath = ("expansion.newbie-helper.protection-disabled.attacker");
                sendMessageWithPrefix(attacker, messagePath, null);
            }
        }
    }

    private NewbieHelperExpansion getNewbieHelperExpansion() {
        return this.expansion;
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("config.yml");
    }

    private boolean shouldRemoveProtectionOnAttack() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("remove-protection-on-attack", true);
    }

    private boolean isMobProtectionEnabled() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("mob-protection", false);
    }

    private Entity getDamager(EntityDamageByEntityEvent e) {
        ConfigurationManager configurationManager = getPluginConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        Entity damager = e.getDamager();

        if (configuration.getBoolean("link-projectiles")) {
            ICombatLogX plugin = getCombatLogX();
            damager = EntityHelper.linkProjectile(plugin, damager);
        }

        if (configuration.getBoolean("link-pets")) {
            damager = EntityHelper.linkPet(damager);
        }

        return damager;
    }
}

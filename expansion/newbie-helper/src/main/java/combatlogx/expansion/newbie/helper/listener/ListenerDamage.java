package combatlogx.expansion.newbie.helper.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionConfigurationManager;
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

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onDamageByMob(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        if(!(damaged instanceof Player)) return;
        Player player = (Player) damaged;

        Entity damager = getDamager(e);
        if(damager instanceof Player) return;

        ProtectionManager protectionManager = this.expansion.getProtectionManager();
        if(!protectionManager.isProtected(player)) return;
        e.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onDamageMob(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        if(damaged instanceof Player) return;

        Entity damager = getDamager(e);
        if(!(damager instanceof Player)) return;
        Player player = (Player) damager;

        ProtectionManager protectionManager = this.expansion.getProtectionManager();
        LanguageManager languageManager = getLanguageManager();

        if(protectionManager.isProtected(player) && checkRemoveProtectionOnAttack()) {
            protectionManager.setProtected(player, false);
            languageManager.sendMessage(player, "expansion.newbie-helper.protection-disabled.attacker", null, true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onDamageByPlayer(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        if(!(damaged instanceof Player)) return;
        Player attacked = (Player) damaged;

        Entity damager = getDamager(e);
        if(!(damager instanceof Player)) return;
        Player attacker = (Player) damager;

        LanguageManager languageManager = getLanguageManager();
        PVPManager pvpManager = this.expansion.getPVPManager();
        ProtectionManager protectionManager = this.expansion.getProtectionManager();

        if(pvpManager.isDisabled(attacked)) {
            e.setCancelled(true);
            languageManager.sendMessage(attacker, "expansion.newbie-helper.no-pvp.other", null, true);
            return;
        }

        if(pvpManager.isDisabled(attacker)) {
            e.setCancelled(true);
            languageManager.sendMessage(attacker, "expansion.newbie-helper.no-pvp.self", null, true);
            return;
        }

        if(protectionManager.isProtected(attacked)) {
            e.setCancelled(true);
            languageManager.sendMessage(attacker, "expansion.newbie-helper.no-pvp.protected", null, true);
            return;
        }

        if(protectionManager.isProtected(attacker) && checkRemoveProtectionOnAttack()) {
            protectionManager.setProtected(attacker, false);
            languageManager.sendMessage(attacker, "expansion.newbie-helper.protection-disabled.attacker", null, true);
        }
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

    private boolean checkRemoveProtectionOnAttack() {
        ExpansionConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("remove-protection-on-attack");
    }
}
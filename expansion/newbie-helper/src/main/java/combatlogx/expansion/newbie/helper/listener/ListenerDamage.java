package combatlogx.expansion.newbie.helper.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.SirBlobman.api.configuration.ConfigurationManager;
import com.SirBlobman.api.language.LanguageManager;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.SirBlobman.combatlogx.api.expansion.ExpansionListener;
import com.SirBlobman.combatlogx.api.utility.EntityHelper;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;

public final class ListenerDamage extends ExpansionListener {
    private final NewbieHelperExpansion expansion;
    public ListenerDamage(NewbieHelperExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority= EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamage(EntityDamageByEntityEvent e) {
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
package combatlogx.expansion.compatibility.essentials.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import combatlogx.expansion.compatibility.essentials.EssentialsExpansion;
import net.ess3.api.IUser;
import net.ess3.api.events.TPARequestEvent;

public final class ListenerEssentials extends ExpansionListener {
    public ListenerEssentials(EssentialsExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onTeleportRequest(TPARequestEvent e) {
        if(isTeleportRequestEnabled()) return;

        ICombatManager combatManager = getCombatManager();
        LanguageManager languageManager = getLanguageManager();

        CommandSource requester = e.getRequester();
        Player player = requester.getPlayer();
        if(player == null) return;

        if(combatManager.isInCombat(player)) {
            languageManager.sendMessage(player,
                    "expansion.essentials-compatibility.prevent-teleport-request-self", null,
                    true);
            e.setCancelled(true);
            return;
        }

        IUser targetUser = e.getTarget();
        Player target = targetUser.getBase();
        if(target == null) return;

        if(combatManager.isInCombat(target)) {
            languageManager.sendMessage(player,
                    "expansion.essentials-compatibility.prevent-teleport-request-other", null,
                    true);
            e.setCancelled(true);
            // return;
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeCombat(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        if(isVanished(player) && preventVanishSelfTag()) {
            e.setCancelled(true);
        }

        LivingEntity enemy = e.getEnemy();
        if(enemy instanceof Player) {
            Player other = (Player) enemy;
            if(isVanished(other) && preventVanishOtherTag()) {
                e.setCancelled(true);
            }
        }
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("config.yml");
    }

    private boolean isTeleportRequestEnabled() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-teleport-request");
    }

    private boolean preventVanishSelfTag() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("prevent-vanish-tagging-self");
    }

    private boolean preventVanishOtherTag() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("prevent-vanish-tagging-other");
    }

    private boolean isVanished(Player player) {
        Essentials plugin = JavaPlugin.getPlugin(Essentials.class);
        User user = plugin.getUser(player);
        return user.isVanished();
    }
}

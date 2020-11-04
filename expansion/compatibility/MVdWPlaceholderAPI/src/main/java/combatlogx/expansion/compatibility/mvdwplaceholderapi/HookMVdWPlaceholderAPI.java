package combatlogx.expansion.compatibility.mvdwplaceholderapi;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.SirBlobman.api.language.LanguageManager;
import com.SirBlobman.api.utility.Validate;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.Expansion.State;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;

import static com.SirBlobman.combatlogx.api.utility.PlaceholderHelper.*;

public final class HookMVdWPlaceholderAPI implements PlaceholderReplacer {
    private final MVdWPlaceholderAPIExpansion expansion;
    public HookMVdWPlaceholderAPI(MVdWPlaceholderAPIExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    public void register() {
        ICombatLogX combatLogX = this.expansion.getPlugin();
        JavaPlugin plugin = combatLogX.getPlugin();

        List<String> placeholderList = Arrays.asList("time_left", "in_combat", "status", "enemy_name", "enemy_health", "enemy_health_rounded", "enemy_hearts", "newbie_helper_pvp_status", "newbie_helper_protected");
        for(String value : placeholderList) {
            String placeholder = ("combatlogx_" + value);
            PlaceholderAPI.registerPlaceholder(plugin, placeholder, this);
        }
    }

    @Override
    public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
        ICombatLogX plugin = this.expansion.getPlugin();
        Player player = e.getPlayer();
        if(player == null) return null;

        String id = e.getPlaceholder();
        if(!id.startsWith("combatlogx_")) return null;
        String placeholder = id.substring("combatlogx_".length());

        switch(placeholder) {
            case "time_left": return getTimeLeft(plugin, player);
            case "in_combat": return getInCombat(plugin, player);
            case "status": return getStatus(plugin, player);
            case "enemy_name": return getEnemyName(plugin, player);
            case "enemy_health": return getEnemyHealth(plugin, player);
            case "enemy_health_rounded": return getEnemyHealthRounded(plugin, player);
            case "enemy_hearts": return getEnemyHearts(plugin, player);
            case "newbie_helper_pvp_status": return getNewbieHelperPVPStatus(player);
            case "newbie_helper_protected": return getNewbieHelperProtected(player);
            default: break;
        }

        return null;
    }

    private String getNewbieHelperPVPStatus(Player player) {
        ICombatLogX plugin = this.expansion.getPlugin();
        LanguageManager languageManager = plugin.getLanguageManager();
        boolean pvp = true;

        Expansion expansion = getNewbieHelper();
        if(expansion != null) {
            NewbieHelperExpansion newbieHelperExpansion = (NewbieHelperExpansion) expansion;
            PVPManager pvpManager = newbieHelperExpansion.getPVPManager();
            pvp = !pvpManager.isDisabled(player);
        }

        String messagePath = ("placeholder.pvp-status." + (pvp ? "enabled" : "disabled"));
        return languageManager.getMessageColored(player, messagePath);
    }

    private String getNewbieHelperProtected(Player player) {
        boolean isProtected = false;

        Expansion expansion = getNewbieHelper();
        if(expansion != null) {
            NewbieHelperExpansion newbieHelperExpansion = (NewbieHelperExpansion) expansion;
            ProtectionManager protectionManager = newbieHelperExpansion.getProtectionManager();
            isProtected = protectionManager.isProtected(player);
        }

        return Boolean.toString(isProtected);
    }

    private Expansion getNewbieHelper() {
        ICombatLogX plugin = this.expansion.getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();

        Optional<Expansion> optionalExpansion = expansionManager.getExpansion("NewbieHelper");
        if(optionalExpansion.isPresent()) {
            Expansion expansion = optionalExpansion.get();
            State state = expansion.getState();
            if(state == State.ENABLED) return expansion;
        }

        return null;
    }
}
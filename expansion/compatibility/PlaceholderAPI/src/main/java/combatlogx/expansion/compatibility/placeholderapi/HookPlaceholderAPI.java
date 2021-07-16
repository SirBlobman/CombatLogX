package combatlogx.expansion.compatibility.placeholderapi;

import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.Expansion.State;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionDescription;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyDisplayName;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyHealth;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyHealthRounded;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyHearts;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyName;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyWorld;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyX;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyY;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyZ;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getInCombat;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getStatus;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getTimeLeft;

public final class HookPlaceholderAPI extends PlaceholderExpansion {
    private final PlaceholderAPIExpansion expansion;

    public HookPlaceholderAPI(PlaceholderAPIExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        PluginDescriptionFile descriptionFile = getDescriptionFile();
        String pluginName = descriptionFile.getName();
        return pluginName.toLowerCase();
    }

    @Override
    public String getAuthor() {
        PluginDescriptionFile descriptionFile = getDescriptionFile();
        List<String> authorList = descriptionFile.getAuthors();
        return String.join(", ", authorList);
    }

    @Override
    public String getVersion() {
        ExpansionDescription description = this.expansion.getDescription();
        return description.getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        ICombatLogX plugin = this.expansion.getPlugin();

        switch(placeholder) {
            case "time_left": return getTimeLeft(plugin, player);
            case "in_combat": return getInCombat(plugin, player);
            case "status": return getStatus(plugin, player);
            case "enemy_name": return getEnemyName(plugin, player);
            case "enemy_display_name": return getEnemyDisplayName(plugin, player);
            case "enemy_health": return getEnemyHealth(plugin, player);
            case "enemy_health_rounded": return getEnemyHealthRounded(plugin, player);
            case "enemy_hearts": return getEnemyHearts(plugin, player);
            case "enemy_world": return getEnemyWorld(plugin, player);
            case "enemy_x": return getEnemyX(plugin, player);
            case "enemy_y": return getEnemyY(plugin, player);
            case "enemy_z": return getEnemyZ(plugin, player);
            case "newbie_helper_pvp_status": return getNewbieHelperPVPStatus(player);
            case "newbie_helper_protected": return getNewbieHelperProtected(player);
            default: break;
        }

        return null;
    }

    private PluginDescriptionFile getDescriptionFile() {
        ICombatLogX combatLogX = this.expansion.getPlugin();
        JavaPlugin plugin = combatLogX.getPlugin();
        return plugin.getDescription();
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
        return languageManager.getMessage(player, messagePath, null, true);
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

package combatlogx.expansion.compatibility.placeholderapi;

import java.util.List;
import java.util.Optional;

import com.SirBlobman.api.language.LanguageManager;
import com.SirBlobman.api.utility.Validate;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.Expansion.State;
import com.SirBlobman.combatlogx.api.expansion.ExpansionDescription;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

import static com.SirBlobman.combatlogx.api.utility.PlaceholderHelper.*;

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

    @NotNull
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
            case "enemy_health": return getEnemyHealth(plugin, player);
            case "enemy_health_rounded": return getEnemyHealthRounded(plugin, player);
            case "enemy_hearts": return getEnemyHearts(plugin, player);
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
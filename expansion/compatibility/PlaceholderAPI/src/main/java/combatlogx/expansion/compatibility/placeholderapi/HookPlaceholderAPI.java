package combatlogx.expansion.compatibility.placeholderapi;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;

import org.bukkit.entity.Entity;
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
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getCurrentEnemy;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyCount;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyDisplayName;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyHealth;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyHealthRounded;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyHearts;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyHeartsCount;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyName;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyType;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyWorld;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyX;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyY;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyZ;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getInCombat;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getPunishmentCount;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getSpecificEnemy;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getStatus;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getTagCount;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getTimeLeft;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getTimeLeftDecimal;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getTimeLeftDecimalSpecific;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getTimeLeftSpecific;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getUnknownEnemy;

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

    @NotNull
    @Override
    public String getIdentifier() {
        PluginDescriptionFile descriptionFile = getDescriptionFile();
        String pluginName = descriptionFile.getName();
        return pluginName.toLowerCase();
    }

    @NotNull
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

        switch (placeholder) {
            case "tag_count":
                return getTagCount(plugin, player);
            case "enemy_count":
                return getEnemyCount(plugin, player);
            case "punishment_count":
                return getPunishmentCount(plugin, player);
            case "time_left":
                return getTimeLeft(plugin, player);
            case "time_left_decimal":
                return getTimeLeftDecimal(plugin, player);
            case "in_combat":
                return getInCombat(plugin, player);
            case "status":
                return getStatus(plugin, player);
            case "newbie_helper_pvp_status":
                return getNewbieHelperPVPStatus(player);
            case "newbie_helper_protected":
                return getNewbieHelperProtected(player);
            default:
                break;
        }

        if (placeholder.startsWith("time_left_")) {
            String numberString = placeholder.substring("time_left_".length());
            try {
                int index = (Integer.parseInt(numberString) - 1);
                return getTimeLeftSpecific(plugin, player, index);
            } catch (NumberFormatException ignored) {
                // Do Nothing
            }
        }

        if (placeholder.startsWith("time_left_decimal_")) {
            String numberString = placeholder.substring("time_left_decimal_".length());
            try {
                int index = (Integer.parseInt(numberString) - 1);
                return getTimeLeftDecimalSpecific(plugin, player, index);
            } catch (NumberFormatException ignored) {
                // Do Nothing
            }
        }

        if (placeholder.startsWith("current_enemy_")) {
            Entity currentEnemy = getCurrentEnemy(plugin, player);
            String enemyPlaceholder = placeholder.substring("current_enemy_".length());
            switch (enemyPlaceholder) {
                case "name":
                    return getEnemyName(plugin, player, currentEnemy);
                case "type":
                    return getEnemyType(plugin, player, currentEnemy);
                case "display_name":
                    return getEnemyDisplayName(plugin, player, currentEnemy);
                case "health":
                    return getEnemyHealth(plugin, player, currentEnemy);
                case "health_rounded":
                    return getEnemyHealthRounded(plugin, player, currentEnemy);
                case "hearts":
                    return getEnemyHearts(plugin, player, currentEnemy);
                case "hearts_count":
                    return getEnemyHeartsCount(plugin, player, currentEnemy);
                case "world":
                    return getEnemyWorld(plugin, player, currentEnemy);
                case "x":
                    return getEnemyX(plugin, player, currentEnemy);
                case "y":
                    return getEnemyY(plugin, player, currentEnemy);
                case "z":
                    return getEnemyZ(plugin, player, currentEnemy);
                default:
                    break;
            }

            return getEnemyPlaceholder(player, enemyPlaceholder, currentEnemy);
        }

        if (placeholder.startsWith("specific_enemy_")) {
            String subPlaceholder = placeholder.substring("specific_enemy_".length());
            int nextUnderscore = subPlaceholder.indexOf('_');
            if (nextUnderscore == -1) {
                return null;
            }

            try {
                String enemyIdString = subPlaceholder.substring(0, nextUnderscore);
                int index = (Integer.parseInt(enemyIdString) - 1);

                Entity specificEnemy = getSpecificEnemy(plugin, player, index);
                String enemyPlaceholder = subPlaceholder.substring(nextUnderscore + 1);
                switch (enemyPlaceholder) {
                    case "name":
                        return getEnemyName(plugin, player, specificEnemy);
                    case "type":
                        return getEnemyType(plugin, player, specificEnemy);
                    case "display_name":
                        return getEnemyDisplayName(plugin, player, specificEnemy);
                    case "health":
                        return getEnemyHealth(plugin, player, specificEnemy);
                    case "health_rounded":
                        return getEnemyHealthRounded(plugin, player, specificEnemy);
                    case "hearts":
                        return getEnemyHearts(plugin, player, specificEnemy);
                    case "hearts_count":
                        return getEnemyHeartsCount(plugin, player, specificEnemy);
                    case "world":
                        return getEnemyWorld(plugin, player, specificEnemy);
                    case "x":
                        return getEnemyX(plugin, player, specificEnemy);
                    case "y":
                        return getEnemyY(plugin, player, specificEnemy);
                    case "z":
                        return getEnemyZ(plugin, player, specificEnemy);
                    default:
                        break;
                }
            } catch (NumberFormatException ignored) {
                // Do Nothing
            }
        }

        return null;
    }

    private PluginDescriptionFile getDescriptionFile() {
        ICombatLogX combatLogX = this.expansion.getPlugin();
        JavaPlugin plugin = combatLogX.getPlugin();
        return plugin.getDescription();
    }

    private String getEnemyPlaceholder(Player player, String enemyPlaceholder, Entity enemy) {
        ICombatLogX plugin = this.expansion.getPlugin();
        if (enemy == null) {
            return getUnknownEnemy(plugin, player);
        }

        if (enemy instanceof Player) {
            Player playerEnemy = (Player) enemy;
            String placeholder = String.format(Locale.US, "{%s}", enemyPlaceholder);
            return PlaceholderAPI.setBracketPlaceholders(playerEnemy, placeholder);
        }

        return getUnknownEnemy(plugin, player);
    }

    private String getNewbieHelperPVPStatus(Player player) {
        ICombatLogX plugin = this.expansion.getPlugin();
        LanguageManager languageManager = plugin.getLanguageManager();
        boolean pvp = true;

        Expansion expansion = getNewbieHelper();
        if (expansion != null) {
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
        if (expansion != null) {
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
        if (optionalExpansion.isPresent()) {
            Expansion expansion = optionalExpansion.get();
            State state = expansion.getState();
            if (state == State.ENABLED) {
                return expansion;
            }
        }

        return null;
    }
}

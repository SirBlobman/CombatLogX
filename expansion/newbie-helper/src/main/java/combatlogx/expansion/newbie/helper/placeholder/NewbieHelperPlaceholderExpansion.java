package combatlogx.expansion.newbie.helper.placeholder;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.placeholder.IPlaceholderExpansion;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;

public final class NewbieHelperPlaceholderExpansion implements IPlaceholderExpansion {
    private final NewbieHelperExpansion expansion;

    public NewbieHelperPlaceholderExpansion(NewbieHelperExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    @Override
    public ICombatLogX getCombatLogX() {
        NewbieHelperExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    @Override
    public String getId() {
        return "newbie";
    }

    @Override
    public String getReplacement(Player player, List<Entity> enemyList, String placeholder) {
        printDebug("Detected getReplacement for placeholder " + placeholder + " and player " + player.getName());

        switch (placeholder) {
            case "helper_pvp_status":
                return getPvpStatus(player);
            case "helper_protected":
                return getProtected(player);
            default:
                break;
        }

        printDebug("Placeholder is not valid, ignoring.");
        return null;
    }

    private NewbieHelperExpansion getExpansion() {
        return this.expansion;
    }

    private String getPvpStatus(Player player) {
        printDebug("Detected PVP Status placeholder.");

        NewbieHelperExpansion expansion = getExpansion();
        PVPManager pvpManager = expansion.getPVPManager();
        boolean pvp = !pvpManager.isDisabled(player);
        printDebug("PVP Value: " + pvp);

        ICombatLogX combatLogX = getCombatLogX();
        LanguageManager languageManager = combatLogX.getLanguageManager();
        String messagePath = ("placeholder.pvp-status." + (pvp ? "enabled" : "disabled"));
        return languageManager.getMessageString(player, messagePath, null);
    }

    private String getProtected(Player player) {
        printDebug("Detected Protected placeholder.");
        NewbieHelperExpansion expansion = getExpansion();
        ProtectionManager protectionManager = expansion.getProtectionManager();
        boolean isProtected = protectionManager.isProtected(player);
        printDebug("Protected: " + isProtected);
        return Boolean.toString(isProtected);
    }

    private void printDebug(String message) {
        ICombatLogX combatLogX = getCombatLogX();
        ConfigurationManager configurationManager = combatLogX.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if(!configuration.getBoolean("debug-mode", false)) {
            return;
        }

        NewbieHelperExpansion expansion = getExpansion();
        Logger logger = expansion.getLogger();
        logger.info("[Debug] [Placeholders] " + message);
    }
}

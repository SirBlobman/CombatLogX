package combatlogx.expansion.newbie.helper.placeholder;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.utility.MessageUtility;
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
        return "newbie_helper";
    }

    @Override
    public String getReplacement(Player player, List<Entity> enemyList, String placeholder) {
        switch (placeholder) {
            case "pvp_status":
                return getPvpStatus(player);
            case "protected":
                return getProtected(player);
            default:
                break;
        }

        return null;
    }

    private NewbieHelperExpansion getExpansion() {
        return this.expansion;
    }

    private String getPvpStatus(Player player) {
        NewbieHelperExpansion expansion = getExpansion();
        PVPManager pvpManager = expansion.getPVPManager();
        boolean pvp = !pvpManager.isDisabled(player);
        String messagePath = ("placeholder.pvp-status." + (pvp ? "enabled" : "disabled"));

        ICombatLogX combatLogX = getCombatLogX();
        LanguageManager languageManager = combatLogX.getLanguageManager();
        String messageString = languageManager.getMessageString(player, messagePath, null);
        return MessageUtility.color(messageString);
    }

    private String getProtected(Player player) {
        NewbieHelperExpansion expansion = getExpansion();
        ProtectionManager protectionManager = expansion.getProtectionManager();
        boolean isProtected = protectionManager.isProtected(player);
        return Boolean.toString(isProtected);
    }
}

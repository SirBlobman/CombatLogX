package combatlogx.expansion.newbie.helper.manager;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.ComponentReplacer;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;

public final class PVPManager {
    private final NewbieHelperExpansion expansion;

    public PVPManager(NewbieHelperExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    public void setPVP(Player player, boolean pvp) {
        if (isNPC(player)) {
            return;
        }

        YamlConfiguration playerData = getPlayerData(player);
        playerData.set("newbie-helper.pvp-toggle", pvp);
        savePlayerData(player);
    }

    public boolean isDisabled(Player player) {
        if (isNPC(player)) {
            return false;
        }

        YamlConfiguration configuration = getConfiguration();
        boolean defaultPvpState = configuration.getBoolean("pvp-toggle-default-status", true);

        YamlConfiguration playerData = getPlayerData(player);
        return !playerData.getBoolean("newbie-helper.pvp-toggle", defaultPvpState);
    }

    public void sendToggleMessage(Player player) {
        boolean pvpDisabled = isDisabled(player);
        String pvpStatusPath = ("placeholder.toggle." + (pvpDisabled ? "disabled" : "enabled"));

        LanguageManager languageManager = getLanguageManager();
        Component pvpStatus = languageManager.getMessage(player, pvpStatusPath);

        Replacer replacer = new ComponentReplacer("{status}", pvpStatus);
        languageManager.sendMessageWithPrefix(player, "expansion.newbie-helper.togglepvp.self", replacer);
    }

    public void sendAdminToggleMessage(CommandSender sender, Player target) {
        boolean pvpDisabled = isDisabled(target);
        String pvpStatusPath = ("placeholder.toggle." + (pvpDisabled ? "disabled" : "enabled"));

        LanguageManager languageManager = getLanguageManager();
        Component pvpStatus = languageManager.getMessage(target, pvpStatusPath);
        Replacer statusReplacer = new ComponentReplacer("{status}", pvpStatus);

        String targetName = target.getName();
        Replacer targetReplacer = new StringReplacer("{target}", targetName);
        languageManager.sendMessageWithPrefix(sender, "expansion.newbie-helper.togglepvp.admin",
                statusReplacer, targetReplacer);
    }

    private NewbieHelperExpansion getExpansion() {
        return this.expansion;
    }

    private ConfigurationManager getConfigurationManager() {
        NewbieHelperExpansion expansion = getExpansion();
        return expansion.getConfigurationManager();
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getConfigurationManager();
        return configurationManager.get("config.yml");
    }

    private ICombatLogX getCombatLogX() {
        NewbieHelperExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private LanguageManager getLanguageManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLanguageManager();
    }

    private PlayerDataManager getPlayerDataManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlayerDataManager();
    }

    private YamlConfiguration getPlayerData(Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        return playerDataManager.get(player);
    }

    private void savePlayerData(Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        playerDataManager.save(player);
    }

    private boolean isNPC(Player player) {
        if (player == null) {
            return true;
        }

        return player.hasMetadata("NPC");
    }
}

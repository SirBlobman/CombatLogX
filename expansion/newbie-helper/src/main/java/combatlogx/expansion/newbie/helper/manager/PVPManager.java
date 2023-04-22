package combatlogx.expansion.newbie.helper.manager;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.ComponentReplacer;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.api.shaded.adventure.text.Component;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.configuration.NewbieHelperConfiguration;

public final class PVPManager {
    private final NewbieHelperExpansion expansion;

    public PVPManager(@NotNull NewbieHelperExpansion expansion) {
        this.expansion = expansion;
    }

    public void setPVP(@NotNull Player player, boolean pvp) {
        if (isNPC(player)) {
            return;
        }

        YamlConfiguration playerData = getPlayerData(player);
        playerData.set("newbie-helper.pvp-toggle", pvp);
        savePlayerData(player);
    }

    public boolean isDisabled(@NotNull Player player) {
        if (isNPC(player)) {
            return false;
        }

        NewbieHelperConfiguration configuration = getConfiguration();
        boolean defaultPvpState = configuration.getPvpToggleDefaultStatus();

        YamlConfiguration playerData = getPlayerData(player);
        return !playerData.getBoolean("newbie-helper.pvp-toggle", defaultPvpState);
    }

    public void sendToggleMessage(@NotNull Player player) {
        boolean pvpDisabled = isDisabled(player);
        String pvpStatusPath = ("placeholder.toggle." + (pvpDisabled ? "disabled" : "enabled"));

        LanguageManager languageManager = getLanguageManager();
        Component pvpStatus = languageManager.getMessage(player, pvpStatusPath);

        Replacer replacer = new ComponentReplacer("{status}", pvpStatus);
        languageManager.sendMessageWithPrefix(player, "expansion.newbie-helper.togglepvp.self", replacer);
    }

    public void sendAdminToggleMessage(@NotNull CommandSender sender, @NotNull Player target) {
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

    private @NotNull NewbieHelperExpansion getExpansion() {
        return this.expansion;
    }

    private @NotNull NewbieHelperConfiguration getConfiguration() {
        NewbieHelperExpansion expansion = getExpansion();
        return expansion.getConfiguration();
    }

    private @NotNull ICombatLogX getCombatLogX() {
        NewbieHelperExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private @NotNull LanguageManager getLanguageManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLanguageManager();
    }

    private @NotNull PlayerDataManager getPlayerDataManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlayerDataManager();
    }

    private @NotNull YamlConfiguration getPlayerData(@NotNull Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        return playerDataManager.get(player);
    }

    private void savePlayerData(@NotNull Player player) {
        PlayerDataManager playerDataManager = getPlayerDataManager();
        playerDataManager.save(player);
    }

    private boolean isNPC(@NotNull Player player) {
        return player.hasMetadata("NPC");
    }
}

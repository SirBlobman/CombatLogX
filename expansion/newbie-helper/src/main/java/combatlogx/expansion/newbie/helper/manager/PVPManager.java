package combatlogx.expansion.newbie.helper.manager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;

public final class PVPManager {
    private final NewbieHelperExpansion expansion;

    public PVPManager(NewbieHelperExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    public void setPVP(Player player, boolean pvp) {
        Validate.notNull(player, "player must not be null!");
        if (player.hasMetadata("NPC")) {
            return;
        }

        ICombatLogX plugin = this.expansion.getPlugin();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);

        playerData.set("newbie-helper.pvp-toggle", pvp);
        playerDataManager.save(player);
    }

    public boolean isDisabled(Player player) {
        Validate.notNull(player, "player must not be null!");
        if (player.hasMetadata("NPC")) {
            return false;
        }

        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        boolean defaultPvpState = configuration.getBoolean("pvp-toggle-default-status", true);

        ICombatLogX plugin = this.expansion.getPlugin();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);

        return !playerData.getBoolean("newbie-helper.pvp-toggle", defaultPvpState);
    }
}

package combatlogx.expansion.newbie.helper.manager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;

public final class ProtectionManager {
    private final NewbieHelperExpansion expansion;

    public ProtectionManager(NewbieHelperExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    public void setProtected(Player player, boolean protect) {
        Validate.notNull(player, "player must not be null!");
        if (player.hasMetadata("NPC")) {
            return;
        }

        ICombatLogX plugin = this.expansion.getPlugin();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);

        if (!protect) {
            playerData.set("newbie-helper.protected", false);
            playerData.set("newbie-helper.protection-expire-time", null);
            playerDataManager.save(player);
            return;
        }

        long newExpireTime = getProtectionExpireTime();
        playerData.set("newbie-helper.protected", true);
        playerData.set("newbie-helper.protection-expire-time", newExpireTime);
        playerDataManager.save(player);
    }

    public boolean isProtected(Player player) {
        Validate.notNull(player, "player must not be null!");
        if (player.hasMetadata("NPC")) {
            return false;
        }

        ICombatLogX plugin = this.expansion.getPlugin();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        if (!configuration.getBoolean("newbie-helper.protected")) {
            return false;
        }

        long expireTime = configuration.getLong("newbie-helper.protection-expire-time", 0L);
        long systemTime = System.currentTimeMillis();
        if (systemTime < expireTime) {
            return true;
        }

        setProtected(player, false);
        LanguageManager languageManager = plugin.getLanguageManager();
        languageManager.sendMessage(player, "expansion.newbie-helper.protection-disabled.expired", null);
        return false;
    }

    private long getProtectionExpireTime() {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        long protectionMillis = configuration.getLong("protection-time");
        return (System.currentTimeMillis() + protectionMillis);
    }
}

package combatlogx.expansion.newbie.helper.manager;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.configuration.NewbieHelperConfiguration;

public final class ProtectionManager {
    private final NewbieHelperExpansion expansion;

    public ProtectionManager(@NotNull NewbieHelperExpansion expansion) {
        this.expansion = expansion;
    }

    public void setProtected(@NotNull Player player, boolean protect) {
        if (isNPC(player)) {
            return;
        }

        YamlConfiguration playerData = getPlayerData(player);
        if (protect) {
            long newExpireTime = getProtectionExpireTime();
            playerData.set("newbie-helper.protection-expire-time", newExpireTime);
        } else {
            playerData.set("newbie-helper.protection-expire-time", null);
        }

        playerData.set("newbie-helper.protected", protect);
        savePlayerData(player);
    }

    public boolean isProtected(@NotNull Player player) {
        if (isNPC(player)) {
            return false;
        }

        YamlConfiguration configuration = getPlayerData(player);
        if (!configuration.getBoolean("newbie-helper.protected")) {
            return false;
        }

        long expireTime = configuration.getLong("newbie-helper.protection-expire-time", 0L);
        long systemTime = System.currentTimeMillis();
        if (systemTime < expireTime) {
            return true;
        }

        setProtected(player, false);
        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessage(player, "expansion.newbie-helper.protection-disabled.expired");
        return false;
    }

    public long getProtectionExpireTime(Player player) {
        if (isNPC(player)) {
            return 0L;
        }

        YamlConfiguration configuration = getPlayerData(player);
        if (!configuration.getBoolean("newbie-helper.protected")) {
            return 0L;
        }

        return configuration.getLong("newbie-helper.protection-expire-time", 0L);
    }

    private long getProtectionExpireTime() {
        NewbieHelperConfiguration configuration = getConfiguration();
        long protectionMillis = configuration.getProtectionTime();
        return (System.currentTimeMillis() + protectionMillis);
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

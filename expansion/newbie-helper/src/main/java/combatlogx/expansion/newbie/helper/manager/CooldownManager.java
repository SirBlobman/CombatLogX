package combatlogx.expansion.newbie.helper.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.language.SimpleReplacer;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;

public final class CooldownManager {
    private final NewbieHelperExpansion expansion;
    private final Map<UUID, Long> cooldownMap;

    public CooldownManager(NewbieHelperExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
        this.cooldownMap = new ConcurrentHashMap<>();
    }

    public boolean hasCooldown(Player player) {
        Validate.notNull(player, "player must not be null!");

        UUID playerId = player.getUniqueId();
        if(!this.cooldownMap.containsKey(playerId)) {
            return false;
        }

        long systemMillis = System.currentTimeMillis();
        long expireMillis = this.cooldownMap.get(playerId);
        if(systemMillis >= expireMillis) {
            removeCooldown(player);
            return false;
        }

        return true;
    }

    public void addCooldown(Player player) {
        Validate.notNull(player, "player must not be null!");

        long cooldownSeconds = getCooldownSeconds();
        if (cooldownSeconds <= 0L) {
            return;
        }

        long systemTimeMillis = System.currentTimeMillis();
        long cooldownMillis = TimeUnit.SECONDS.toMillis(cooldownSeconds);
        long cooldownExpireMillis = (systemTimeMillis + cooldownMillis);
        setCooldownExpireMillis(player, cooldownExpireMillis);
    }

    public void setCooldownExpireMillis(Player player, long expireMillis) {
        Validate.notNull(player, "player must not be null!");

        UUID playerId = player.getUniqueId();
        this.cooldownMap.put(playerId, expireMillis);
    }

    public long getCooldownExpireMillis(Player player) {
        Validate.notNull(player, "player must not be null!");

        UUID playerId = player.getUniqueId();
        return this.cooldownMap.getOrDefault(playerId, 0L);
    }

    public void removeCooldown(Player player) {
        Validate.notNull(player, "player must not be null!");

        UUID playerId = player.getUniqueId();
        this.cooldownMap.remove(playerId);
    }

    public void sendCooldownMessage(Player player) {
        if (!hasCooldown(player)) {
            return;
        }

        long expireMillis = getCooldownExpireMillis(player);
        long systemTimeMillis = System.currentTimeMillis();
        long subtractMillis = (expireMillis - systemTimeMillis);
        long subtractSeconds = TimeUnit.MILLISECONDS.toSeconds(subtractMillis);

        String timeLeftString = Long.toString(subtractSeconds);
        Replacer replacer = new SimpleReplacer("{time_left}", timeLeftString);

        NewbieHelperExpansion expansion = getExpansion();
        ICombatLogX combatLogX = expansion.getPlugin();
        combatLogX.sendMessageWithPrefix(player, "expansion.newbie-helper.togglepvp.cooldown", replacer);
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

    private long getCooldownSeconds() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getLong("pvp-toggle-cooldown", 0L);
    }
}

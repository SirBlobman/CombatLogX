package combatlogx.expansion.newbie.helper.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.LongReplacer;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.configuration.NewbieHelperConfiguration;

public final class CooldownManager {
    private final NewbieHelperExpansion expansion;
    private final Map<UUID, Long> cooldownMap;

    public CooldownManager(@NotNull NewbieHelperExpansion expansion) {
        this.expansion = expansion;
        this.cooldownMap = new ConcurrentHashMap<>();
    }

    public boolean hasCooldown(@NotNull Player player) {
        NewbieHelperConfiguration configuration = getConfiguration();
        Permission permission = configuration.getPermission();
        if (permission != null && player.hasPermission(permission)) {
            return false;
        }

        UUID playerId = player.getUniqueId();
        if (!this.cooldownMap.containsKey(playerId)) {
            return false;
        }

        long systemMillis = System.currentTimeMillis();
        long expireMillis = this.cooldownMap.get(playerId);
        if (systemMillis >= expireMillis) {
            removeCooldown(player);
            return false;
        }

        return true;
    }

    public void addCooldown(@NotNull Player player) {
        NewbieHelperConfiguration configuration = getConfiguration();
        Permission permission = configuration.getPermission();
        if (permission != null && player.hasPermission(permission)) {
            return;
        }

        long cooldownSeconds = getCooldownSeconds();
        if (cooldownSeconds <= 0L) {
            return;
        }

        long systemTimeMillis = System.currentTimeMillis();
        long cooldownMillis = TimeUnit.SECONDS.toMillis(cooldownSeconds);
        long cooldownExpireMillis = (systemTimeMillis + cooldownMillis);
        setCooldownExpireMillis(player, cooldownExpireMillis);
    }

    public void setCooldownExpireMillis(@NotNull Player player, long expireMillis) {
        UUID playerId = player.getUniqueId();
        this.cooldownMap.put(playerId, expireMillis);
    }

    public long getCooldownExpireMillis(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        return this.cooldownMap.getOrDefault(playerId, 0L);
    }

    public void removeCooldown(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        this.cooldownMap.remove(playerId);
    }

    public void sendCooldownMessage(@NotNull Player player) {
        if (!hasCooldown(player)) {
            return;
        }

        long expireMillis = getCooldownExpireMillis(player);
        long systemTimeMillis = System.currentTimeMillis();
        long subtractMillis = (expireMillis - systemTimeMillis);
        long subtractSeconds = TimeUnit.MILLISECONDS.toSeconds(subtractMillis);
        Replacer replacer = new LongReplacer("{time_left}", subtractSeconds);

        LanguageManager languageManager = getLanguageManager();
        String messageKey = "expansion.newbie-helper.togglepvp.cooldown";
        languageManager.sendMessageWithPrefix(player, messageKey, replacer);
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

    private long getCooldownSeconds() {
        NewbieHelperConfiguration configuration = getConfiguration();
        return configuration.getPvpToggleCooldown();
    }
}

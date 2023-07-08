package combatlogx.expansion.cheat.prevention.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IConfiguration;

public abstract class CheatPreventionListener extends ExpansionListener {
    private final ICheatPreventionExpansion expansion;
    private final Map<UUID, Map<String, Long>> messageCooldownMap;

    public CheatPreventionListener(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion.getExpansion());
        this.expansion = expansion;
        this.messageCooldownMap = new ConcurrentHashMap<>();
    }

    protected final @NotNull ICheatPreventionExpansion getCheatPrevention() {
        return this.expansion;
    }

    protected final void sendMessageIgnoreCooldown(@NotNull Player player, @NotNull String key,
                                                   Replacer @NotNull ... replacer) {
        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessageWithPrefix(player, key, replacer);
        addMessageCooldown(player, key);
    }

    protected final void sendMessage(@NotNull Player player, @NotNull String key, Replacer @NotNull ... replacer) {
        long systemMillis = System.currentTimeMillis();
        long expireMillis = getCooldownExpireTime(player, key);
        if (systemMillis < expireMillis) {
            return;
        }

        sendMessageIgnoreCooldown(player, key, replacer);
    }

    private long getNewMessageCooldownExpireTime() {
        ICheatPreventionExpansion cheatPrevention = getCheatPrevention();
        IConfiguration configuration = cheatPrevention.getConfiguration();
        long cooldownSeconds = configuration.getMessageCooldown();

        long cooldownMillis = TimeUnit.SECONDS.toMillis(cooldownSeconds);
        long systemMillis = System.currentTimeMillis();
        return (systemMillis + cooldownMillis);
    }

    private long getCooldownExpireTime(Player player, String key) {
        UUID playerId = player.getUniqueId();
        Map<String, Long> expireTimeMap = this.messageCooldownMap.getOrDefault(playerId, new HashMap<>());
        return expireTimeMap.getOrDefault(key, 0L);
    }

    private void addMessageCooldown(Player player, String key) {
        UUID playerId = player.getUniqueId();
        Map<String, Long> expireTimeMap = this.messageCooldownMap.getOrDefault(playerId, new HashMap<>());
        expireTimeMap.put(key, getNewMessageCooldownExpireTime());
        this.messageCooldownMap.put(playerId, expireTimeMap);
    }
}

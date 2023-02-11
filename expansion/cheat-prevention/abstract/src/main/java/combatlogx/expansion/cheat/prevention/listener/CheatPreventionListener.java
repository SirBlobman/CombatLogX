package combatlogx.expansion.cheat.prevention.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

public abstract class CheatPreventionListener extends ExpansionListener {
    private final Map<UUID, Map<String, Long>> messageCooldownMap;

    public CheatPreventionListener(Expansion expansion) {
        super(expansion);
        this.messageCooldownMap = new ConcurrentHashMap<>();
    }

    protected final void sendMessageIgnoreCooldown(Player player, String key, Replacer replacer) {
        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessageWithPrefix(player, key, replacer);
        addMessageCooldown(player, key);
    }

    protected final void sendMessage(Player player, String key, Replacer replacer) {
        long systemMillis = System.currentTimeMillis();
        long expireMillis = getCooldownExpireTime(player, key);
        if (systemMillis < expireMillis) {
            return;
        }

        sendMessageIgnoreCooldown(player, key, replacer);
    }

    private long getNewMessageCooldownExpireTime() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        long cooldownSeconds = configuration.getLong("message-cooldown");

        long cooldownMillis = (cooldownSeconds * 1_000L);
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

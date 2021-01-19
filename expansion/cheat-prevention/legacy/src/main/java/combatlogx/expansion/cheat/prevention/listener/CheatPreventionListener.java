package combatlogx.expansion.cheat.prevention.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

public abstract class CheatPreventionListener extends ExpansionListener {
    private final Map<UUID, Map<String, Long>> messageCooldownMap;
    public CheatPreventionListener(Expansion expansion) {
        super(expansion);
        this.messageCooldownMap = new HashMap<>();
    }

    protected final boolean isInCombat(Player player) {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        return combatManager.isInCombat(player);
    }

    protected final void sendMessage(Player player, String key, Replacer replacer) {
        long systemMillis = System.currentTimeMillis();
        long expireMillis = getCooldownExpireTime(player, key);
        if(expireMillis < systemMillis) return;

        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessage(player, key, replacer, true);
        addMessageCooldown(player, key);
    }

    private long getNewMessageCooldownExpireTime() {
        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        long cooldownSeconds = configuration.getLong("message-cooldown");

        long cooldownMillis = (cooldownSeconds * 1_000L);
        long systemMillis = System.currentTimeMillis();
        return (systemMillis + cooldownMillis);
    }

    private long getCooldownExpireTime(Player player, String key) {
        UUID uuid = player.getUniqueId();
        Map<String, Long> expireTimeMap = this.messageCooldownMap.getOrDefault(uuid, new HashMap<>());
        return expireTimeMap.getOrDefault(key, 0L);
    }

    private void addMessageCooldown(Player player, String key) {
        UUID uuid = player.getUniqueId();
        Map<String, Long> expireTimeMap = this.messageCooldownMap.getOrDefault(uuid, new HashMap<>());
        expireTimeMap.put(key, getNewMessageCooldownExpireTime());
        this.messageCooldownMap.put(uuid, expireTimeMap);
    }
}
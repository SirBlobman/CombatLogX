package com.github.sirblobman.combatlogx.listener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.CombatPlugin;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.listener.IDeathListener;

public class ListenerDeath extends CombatListener implements IDeathListener {
    private final Set<UUID> customDeathSet;
    public ListenerDeath(CombatPlugin plugin) {
        super(plugin);
        this.customDeathSet = new HashSet<>();
    }

    @Override
    public void add(Player player) {
        UUID uuid = player.getUniqueId();
        this.customDeathSet.add(uuid);
    }

    @Override
    public void remove(Player player) {
        UUID uuid = player.getUniqueId();
        this.customDeathSet.remove(uuid);
    }

    @Override
    public boolean contains(Player player) {
        UUID uuid = player.getUniqueId();
        return this.customDeathSet.contains(uuid);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        ICombatLogX plugin = getPlugin();
        YamlConfiguration configuration = plugin.getData(player);
        if(!configuration.getBoolean("kill-on-join")) return;

        configuration.set("kill-on-join", false);
        plugin.saveData(player);

        add(player);
        player.setHealth(0.0D);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if(!contains(player)) return;
        remove(player);

        String message = getRandomDeathMessage(player);
        if(message != null) e.setDeathMessage(message);
    }

    private String getRandomDeathMessage(Player player) {
        ICombatLogX plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        List<String> customDeathMessageList = configuration.getStringList("custom-death-message-list");
        if(customDeathMessageList.isEmpty()) return null;

        int customDeathMessageListSize = customDeathMessageList.size();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String customDeathMessage = customDeathMessageList.get(random.nextInt(customDeathMessageListSize));

        String playerName = player.getName();
        return customDeathMessage.replace("{player}", playerName);
    }
}

package com.SirBlobman.combatlogx.listener;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.listener.ICustomDeathListener;
import com.SirBlobman.combatlogx.api.shaded.utility.Util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ListenerCustomDeath implements ICustomDeathListener {
    private final List<UUID> customDeathList = Util.newList();
    private final ICombatLogX plugin;
    public ListenerCustomDeath(ICombatLogX plugin) {
        this.plugin = plugin;
    }

    @Override
    public void add(Player player) {
        if(player == null) return;

        UUID uuid = player.getUniqueId();
        customDeathList.add(uuid);
    }

    @Override
    public void remove(Player player) {
        if(player == null) return;

        UUID uuid = player.getUniqueId();
        customDeathList.remove(uuid);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        YamlConfiguration playerData = this.plugin.getDataFile(player);
        if(!playerData.getBoolean("kill-on-join")) return;

        add(player);
        player.setHealth(0.0D);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        UUID uuid = player.getUniqueId();
        if(!customDeathList.contains(uuid)) return;

        FileConfiguration config = this.plugin.getConfig("config.yml");
        List<String> customDeathMessageList = config.getStringList("punishments.custom-death-messages");
        if(customDeathMessageList.isEmpty()) return;

        int random = ThreadLocalRandom.current().nextInt(customDeathMessageList.size());
        String message = customDeathMessageList.get(random).replace("{name}", player.getName());
        e.setDeathMessage(message);
        remove(player);
    }
}
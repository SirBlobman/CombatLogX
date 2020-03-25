package com.SirBlobman.combatlogx.expansion.damage.tagger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.expansion.damage.tagger.listener.ListenerDamage;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class DamageTagger extends Expansion {
    public DamageTagger(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig("damage-tagger.yml");
    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        ListenerDamage listener = new ListenerDamage(this);
        manager.registerEvents(listener, this.getPlugin().getPlugin());
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        reloadConfig("damage-tagger.yml");
    }
}
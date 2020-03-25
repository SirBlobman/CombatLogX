package com.SirBlobman.combatlogx.expansion.mob.tagger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.expansion.mob.tagger.listener.ListenerMobCombat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MobTagger extends Expansion {
    public MobTagger(ICombatLogX plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad() {
        saveDefaultConfig("mob-tagger.yml");
    }
    
    @Override
    public void onEnable() {
        JavaPlugin plugin = getPlugin().getPlugin();
        PluginManager manager = Bukkit.getPluginManager();
        
        ListenerMobCombat listener = new ListenerMobCombat(this);
        manager.registerEvents(listener, plugin);
    }
    
    @Override
    public void onDisable() {
        // Do Nothing
    }
    
    @Override
    public void reloadConfig() {
        reloadConfig("mob-tagger.yml");
    }
}
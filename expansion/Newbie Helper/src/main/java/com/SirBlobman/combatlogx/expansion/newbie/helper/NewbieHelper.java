package com.SirBlobman.combatlogx.expansion.newbie.helper;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.expansion.newbie.helper.listener.ListenerNewbieProtection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class NewbieHelper extends Expansion {
    public NewbieHelper(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "NewbieHelper";
    }

    @Override
    public String getName() {
        return "Newbie Helper";
    }

    @Override
    public String getVersion() {
        return "15.0";
    }

    @Override
    public void onLoad() {
        saveDefaultConfig("newbie-helper.yml");
    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new ListenerNewbieProtection(this), getPlugin().getPlugin());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void reloadConfig() {
        reloadConfig("newbie-helper.yml");
    }
}
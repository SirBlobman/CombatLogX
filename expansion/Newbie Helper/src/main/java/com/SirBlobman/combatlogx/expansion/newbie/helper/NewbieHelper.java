package com.SirBlobman.combatlogx.expansion.newbie.helper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.expansion.newbie.helper.command.CommandTogglePVP;
import com.SirBlobman.combatlogx.expansion.newbie.helper.listener.ListenerNewbieProtection;
import com.SirBlobman.combatlogx.expansion.newbie.helper.listener.ListenerPVP;

public class NewbieHelper extends Expansion {
    private final ListenerPVP listenerPVP;
    public NewbieHelper(ICombatLogX plugin) {
        super(plugin);
        this.listenerPVP = new ListenerPVP(this);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig("newbie-helper.yml");
    }

    @Override
    public void reloadConfig() {
        reloadConfig("newbie-helper.yml");
    }

    @Override
    public void onEnable() {
        ICombatLogX plugin = getPlugin();
        JavaPlugin javaPlugin = plugin.getPlugin();
        CommandTogglePVP command = new CommandTogglePVP(this);
        plugin.registerCommand("togglepvp", command, "Do you want to PVP or not?", "/<command>", "pvptoggle", "pvp");

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(this.listenerPVP, javaPlugin);
        manager.registerEvents(new ListenerNewbieProtection(this), javaPlugin);
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    public ListenerPVP getPVPListener() {
        return this.listenerPVP;
    }
}
package com.github.sirblobman.combatlogx.api;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.IResourceHolder;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;
import com.github.sirblobman.combatlogx.api.manager.IForgiveManager;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.manager.IPunishManager;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;

public interface ICombatLogX extends IResourceHolder {
    default ICombatLogX getCombatLogX() {
        return this;
    }

    JavaPlugin getPlugin();

    void onReload();

    MultiVersionHandler getMultiVersionHandler();

    ConfigurationManager getConfigurationManager();

    PlayerDataManager getPlayerDataManager();

    LanguageManager getLanguageManager();

    ExpansionManager getExpansionManager();

    ICombatManager getCombatManager();

    ITimerManager getTimerManager();

    IPunishManager getPunishManager();

    IDeathManager getDeathManager();

    IPlaceholderManager getPlaceholderManager();

    IForgiveManager getForgiveManager();

    void sendMessage(CommandSender sender, String... messageArray);

    boolean isDebugModeDisabled();

    void printDebug(String... messageArray);

    void printDebug(Throwable ex);
}

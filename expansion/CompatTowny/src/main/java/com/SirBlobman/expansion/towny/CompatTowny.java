package com.SirBlobman.expansion.towny;

import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.towny.config.ConfigTowny;
import com.SirBlobman.expansion.towny.listener.ListenTowny;

import java.io.File;

public class CompatTowny implements CLXExpansion, Listener {
    public static File FOLDER;

    public String getUnlocalizedName() {
        return "CompatTowny";
    }

    public String getName() {
        return "Towny Compatibility";
    }

    public String getVersion() {
        return "14.2";
    }

    @Override
    public void enable() {
        if (PluginUtil.isEnabled("Towny")) {
            FOLDER = getDataFolder();
            ConfigTowny.load();
            PluginUtil.regEvents(new ListenTowny());
        } else {
            String error = "Towny is not installed. Automatically disabling...";
            print(error);
            Expansions.unloadExpansion(this);
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public void onConfigReload() {
        if (PluginUtil.isEnabled("Towny")) ConfigTowny.load();
    }
}
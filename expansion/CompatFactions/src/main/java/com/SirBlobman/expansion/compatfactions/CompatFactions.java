package com.SirBlobman.expansion.compatfactions;

import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.compatfactions.config.ConfigFactions;
import com.SirBlobman.expansion.compatfactions.listener.ListenFactions;
import com.SirBlobman.expansion.compatfactions.utility.FactionsUtil;

import java.io.File;

public class CompatFactions implements CLXExpansion, Listener {
    public static File FOLDER;
    private static FactionsUtil FUTIL;

    public String getVersion() {
        return "14.2";
    }

    public String getUnlocalizedName() {
        return "CompatFactions";
    }

    public String getName() {
        return "Factions Compatibility";
    }

    @Override
    public void enable() {
        FUTIL = FactionsUtil.getFactionsUtil();
        if (FUTIL == null) {
            String error = "Could not find a valid Factions plugin. Please contact SirBlobman if you think this should not be happening!";
            print(error);
            Expansions.unloadExpansion(this);
        } else {
            FOLDER = getDataFolder();
            ConfigFactions.load();
            PluginUtil.regEvents(new ListenFactions(FUTIL));
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public void onConfigReload() {
        FUTIL = FactionsUtil.getFactionsUtil();
        if (FUTIL != null) {
            FOLDER = getDataFolder();
            ConfigFactions.load();
        }
    }
}
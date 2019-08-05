package com.SirBlobman.expansion.notcombatlogx;

import java.io.File;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.notcombatlogx.config.ConfigNot;
import com.SirBlobman.expansion.notcombatlogx.listener.ListenNot;

public class NotCombatLogX implements CLXExpansion {
    public static File FOLDER;

    public String getUnlocalizedName() {
        return "NotCombatLogX";
    }

    public String getVersion() {
        return "14.3";
    }

    @Override
    public void enable() {
        FOLDER = getDataFolder();
        ConfigNot.load();
        
        ListenNot listener = new ListenNot();
        PluginUtil.regEvents(listener);
    }

    @Override
    public void disable() {
    	// Do Nothing
    }

    @Override
    public void onConfigReload() {
        FOLDER = getDataFolder();
        ConfigNot.load();
    }
}
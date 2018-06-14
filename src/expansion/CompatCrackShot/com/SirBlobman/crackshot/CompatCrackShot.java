package com.SirBlobman.crackshot;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;

public class CompatCrackShot implements CLXExpansion {
    @Override
    public void enable() {
        if (Util.PM.isPluginEnabled("CrackShot")) {
            Util.regEvents(new ListenCrackShot());
        } else {
            String error = "CrackShot is not installed! This expansion is useless.";
            print(error);
        }
    }

    public String getUnlocalizedName() {
        return "CompatCrackShot";
    }

    public String getName() {
        return "CrackShot Compatability";
    }

    public String getVersion() {
        return "2";
    }
}
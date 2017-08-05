package com.SirBlobman.crackshot;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;

public class CompatCrackShot implements CLXExpansion {
    @Override
    public void enable() {
        if(Util.PM.isPluginEnabled("CrackShot")) {
            Util.regEvents(new ListenCrackShot());
        } else {
            String error = "CrackShot is not installed! This expansion is useless.";
            print(error);
        }
    }
    
    @Override
    public String getName() {return "CrackShot Compatability";}
    
    @Override
    public String getVersion() {return "1.0.0";}
}
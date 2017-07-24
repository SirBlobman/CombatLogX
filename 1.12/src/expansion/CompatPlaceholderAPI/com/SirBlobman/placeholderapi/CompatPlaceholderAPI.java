package com.SirBlobman.placeholderapi;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;

public class CompatPlaceholderAPI implements CLXExpansion {
    @Override
    public void enable() {
        if(Util.PM.isPluginEnabled("PlaceholderAPI")) {
            Hook h = new Hook();
            h.hook();
        } else {
            String error = "PlaceholderAPI is not installed. This expansion is useless!";
            Util.print(error);
        }
    }
    
    @Override
    public String getName() {return "PlaceholderAPI Compatability";}
    
    @Override
    public String getVersion() {return "1.0.0";}
}
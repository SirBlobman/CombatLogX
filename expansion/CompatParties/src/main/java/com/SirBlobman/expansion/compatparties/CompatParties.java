package com.SirBlobman.expansion.compatparties;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;

public class CompatParties implements CLXExpansion {
    public String getUnlocalizedName() {
        return "CompatParties";
    }

    public String getName() {
        return "Parties Compatibility";
    }

    public String getVersion() {
        return "14.2";
    }

    @Override
    public void enable() {
        if (!PluginUtil.isEnabled("Parties")) {
            print("Parties is not installed, automatically disabling...");
            Expansions.unloadExpansion(this);
            return;
        }
        
        PluginUtil.regEvents(new ListenParties());
    }

    @Override
    public void disable() {

    }

    @Override
    public void onConfigReload() {

    }
}
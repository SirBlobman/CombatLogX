package com.SirBlobman.expansion.placeholders;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.placeholders.hook.MHook;
import com.SirBlobman.expansion.placeholders.hook.PHook;

public class CompatPlaceholders implements CLXExpansion {
    public String getUnlocalizedName() {
        return "CompatPlaceholders";
    }

    public String getName() {
        return "Placeholder Compatibility";
    }

    public String getVersion() {
        return "13.3";
    }

    @Override
    public void enable() {
        boolean enabled = false;
        if (PluginUtil.isEnabled("PlaceholderAPI")) {
            enabled = true;
            PHook p = new PHook();
            p.register();
        }

        if (PluginUtil.isEnabled("MVdWPlaceholderAPI")) {
            enabled = true;
            MHook m = new MHook();
            m.register();
        }

        if (!enabled) {
            String error = "Could not find PlaceholderAPI or MVdWPlaceholderAPI. Automatically disabling...";
            Expansions.unloadExpansion(this);
            print(error);
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public void onConfigReload() {

    }
}
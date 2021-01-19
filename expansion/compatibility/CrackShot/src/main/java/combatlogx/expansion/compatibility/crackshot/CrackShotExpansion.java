package combatlogx.expansion.compatibility.crackshot;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import combatlogx.expansion.compatibility.crackshot.listener.ListenerCrackShot;

public class CrackShotExpansion extends Expansion {
    public CrackShotExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        if(!checkDependency("CrackShot", true)) {
            ICombatLogX plugin = getPlugin();
            ExpansionManager expansionManager = plugin.getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }

        new ListenerCrackShot(this).register();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void reloadConfig() {

    }
}
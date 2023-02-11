package combatlogx.expansion.compatibility.luckperms;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.luckperms.context.ContextInCombat;
import combatlogx.expansion.compatibility.luckperms.hook.HookNewbieHelper;

public final class LuckPermsExpansion extends Expansion {
    public LuckPermsExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        if (!checkDependency("LuckPerms", true, "5.4")) {
            selfDisable();
            return;
        }

        registerContexts();
        HookNewbieHelper.registerContexts(this);
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        // Do Nothing
    }

    private void registerContexts() {
        new ContextInCombat(this).register();
    }
}

package combatlogx.expansion.compatibility.luckperms.hook;

import java.util.Optional;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import combatlogx.expansion.compatibility.luckperms.LuckPermsExpansion;
import combatlogx.expansion.compatibility.luckperms.context.ContextNewbieHelperProtected;
import combatlogx.expansion.compatibility.luckperms.context.ContextNewbieHelperPvpStatus;
import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;

public final class HookNewbieHelper {
    public static void registerContexts(LuckPermsExpansion instance) {
        ICombatLogX plugin = instance.getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        Optional<Expansion> optionalExpansion = expansionManager.getExpansion("NewbieHelper");
        if (!optionalExpansion.isPresent()) {
            return;
        }

        Expansion expansion = optionalExpansion.get();
        if (!(expansion instanceof NewbieHelperExpansion)) {
            return;
        }

        NewbieHelperExpansion newbieHelper = (NewbieHelperExpansion) expansion;
        new ContextNewbieHelperProtected(instance, newbieHelper).register();
        new ContextNewbieHelperPvpStatus(instance, newbieHelper).register();
    }
}

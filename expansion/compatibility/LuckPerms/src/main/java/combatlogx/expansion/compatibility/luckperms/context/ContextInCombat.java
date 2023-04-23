package combatlogx.expansion.compatibility.luckperms.context;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;

import combatlogx.expansion.compatibility.luckperms.LuckPermsExpansion;
import net.luckperms.api.context.ContextConsumer;

public final class ContextInCombat extends AbstractContext<Player> {
    public ContextInCombat(LuckPermsExpansion expansion) {
        super(expansion);
    }

    @Override
    public void calculate(@NotNull Player target, ContextConsumer consumer) {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        boolean combat = combatManager.isInCombat(target);
        consumer.accept("combatlogx-in-combat", Boolean.toString(combat));
    }
}

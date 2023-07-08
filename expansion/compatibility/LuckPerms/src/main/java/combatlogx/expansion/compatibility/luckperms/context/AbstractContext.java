package combatlogx.expansion.compatibility.luckperms.context;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.compatibility.luckperms.LuckPermsExpansion;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextManager;

public abstract class AbstractContext<T> implements ContextCalculator<T> {
    private final LuckPermsExpansion expansion;

    public AbstractContext(LuckPermsExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    protected final LuckPermsExpansion getExpansion() {
        return this.expansion;
    }

    protected final ICombatLogX getCombatLogX() {
        LuckPermsExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    public final void register() {
        LuckPerms luckPerms = LuckPermsProvider.get();
        ContextManager contextManager = luckPerms.getContextManager();
        contextManager.registerCalculator(this);
    }
}

package combatlogx.expansion.compatibility.luckperms.context;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.Validate;

import combatlogx.expansion.compatibility.luckperms.LuckPermsExpansion;
import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;

public final class ContextNewbieHelperProtected extends AbstractContext<Player> {
    private final NewbieHelperExpansion newbieHelper;

    public ContextNewbieHelperProtected(@NotNull LuckPermsExpansion expansion, @NotNull NewbieHelperExpansion newbieHelper) {
        super(expansion);
        this.newbieHelper = newbieHelper;
    }

    private @NotNull NewbieHelperExpansion getNewbieHelper() {
        return this.newbieHelper;
    }

    @Override
    public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
        NewbieHelperExpansion newbieHelper = getNewbieHelper();
        ProtectionManager protectionManager = newbieHelper.getProtectionManager();
        boolean isProtected = protectionManager.isProtected(target);
        consumer.accept("newbie-helper-pvp-protected", Boolean.toString(isProtected));
    }

    @Override
    public @NotNull ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
        builder.add("newbie-helper-pvp-protected", "false");
        builder.add("newbie-helper-pvp-protected", "true");
        return builder.build();
    }
}

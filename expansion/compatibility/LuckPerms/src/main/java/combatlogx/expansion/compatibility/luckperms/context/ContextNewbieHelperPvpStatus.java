package combatlogx.expansion.compatibility.luckperms.context;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.Validate;

import combatlogx.expansion.compatibility.luckperms.LuckPermsExpansion;
import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;

public final class ContextNewbieHelperPvpStatus extends AbstractContext<Player> {
    private final NewbieHelperExpansion newbieHelper;

    public ContextNewbieHelperPvpStatus(@NotNull LuckPermsExpansion expansion, @NotNull NewbieHelperExpansion newbieHelper) {
        super(expansion);
        this.newbieHelper = Validate.notNull(newbieHelper, "newbieHelper must not be null!");
    }

    private @NotNull NewbieHelperExpansion getNewbieHelper() {
        return this.newbieHelper;
    }

    @Override
    public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
        NewbieHelperExpansion newbieHelper = getNewbieHelper();
        PVPManager pvpManager = newbieHelper.getPVPManager();
        boolean status = !pvpManager.isDisabled(target);
        consumer.accept("newbie-helper-pvp-status", Boolean.toString(status));
    }

    @Override
    public @NotNull ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
        builder.add("newbie-helper-pvp-status", "false");
        builder.add("newbie-helper-pvp-status", "true");
        return builder.build();
    }
}

package combatlogx.expansion.compatibility.luckperms.context;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.Validate;

import combatlogx.expansion.compatibility.luckperms.LuckPermsExpansion;
import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import net.luckperms.api.context.ContextConsumer;

public final class ContextNewbieHelperPvpStatus extends AbstractContext<Player> {
    private final NewbieHelperExpansion newbieHelper;

    public ContextNewbieHelperPvpStatus(LuckPermsExpansion expansion, NewbieHelperExpansion newbieHelper) {
        super(expansion);
        this.newbieHelper = Validate.notNull(newbieHelper, "newbieHelper must not be null!");
    }

    private NewbieHelperExpansion getNewbieHelper() {
        return this.newbieHelper;
    }

    @Override
    public void calculate(@NotNull Player target, ContextConsumer consumer) {
        NewbieHelperExpansion newbieHelper = getNewbieHelper();
        PVPManager pvpManager = newbieHelper.getPVPManager();
        boolean status = !pvpManager.isDisabled(target);
        consumer.accept("newbie-helper-pvp-status", Boolean.toString(status));
    }
}

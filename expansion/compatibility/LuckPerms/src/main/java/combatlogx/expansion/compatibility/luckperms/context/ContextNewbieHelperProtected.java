package combatlogx.expansion.compatibility.luckperms.context;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.Validate;

import combatlogx.expansion.compatibility.luckperms.LuckPermsExpansion;
import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;
import net.luckperms.api.context.ContextConsumer;

public final class ContextNewbieHelperProtected extends AbstractContext<Player> {
    private final NewbieHelperExpansion newbieHelper;

    public ContextNewbieHelperProtected(LuckPermsExpansion expansion, NewbieHelperExpansion newbieHelper) {
        super(expansion);
        this.newbieHelper = Validate.notNull(newbieHelper, "newbieHelper must not be null!");
    }

    private NewbieHelperExpansion getNewbieHelper() {
        return this.newbieHelper;
    }

    @Override
    public void calculate(@NotNull Player target, ContextConsumer consumer) {
        NewbieHelperExpansion newbieHelper = getNewbieHelper();
        ProtectionManager protectionManager = newbieHelper.getProtectionManager();
        boolean isProtected = protectionManager.isProtected(target);
        consumer.accept("newbie-helper-pvp-protected", Boolean.toString(isProtected));
    }
}

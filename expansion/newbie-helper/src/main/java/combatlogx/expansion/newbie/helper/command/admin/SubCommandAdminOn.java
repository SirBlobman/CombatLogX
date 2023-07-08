package combatlogx.expansion.newbie.helper.command.admin;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;

public final class SubCommandAdminOn extends CombatLogCommand {
    private final NewbieHelperExpansion expansion;

    public SubCommandAdminOn(@NotNull NewbieHelperExpansion expansion) {
        super(expansion.getPlugin(), "on");
        setPermissionName("combatlogx.command.togglepvp.admin.on");
        this.expansion = expansion;
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 1) {
            Set<String> valueSet = getOnlinePlayerNames();
            return getMatching(args[0], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length < 1) {
            return false;
        }

        Player target = findTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        NewbieHelperExpansion expansion = getExpansion();
        PVPManager pvpManager = expansion.getPVPManager();
        pvpManager.setPVP(target, true);
        pvpManager.sendAdminToggleMessage(sender, target);
        return true;
    }

    private @NotNull NewbieHelperExpansion getExpansion() {
        return this.expansion;
    }
}

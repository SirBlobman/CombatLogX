package combatlogx.expansion.newbie.helper.command.admin;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;

public final class SubCommandAdmin extends CombatLogCommand {
    public SubCommandAdmin(NewbieHelperExpansion expansion) {
        super(expansion.getPlugin(), "admin");
        setPermissionName("combatlogx.command.togglepvp.admin");
        addSubCommand(new SubCommandAdminOn(expansion));
        addSubCommand(new SubCommandAdminOff(expansion));
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        return false;
    }
}

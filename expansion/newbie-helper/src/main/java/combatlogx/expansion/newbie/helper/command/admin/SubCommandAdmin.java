package combatlogx.expansion.newbie.helper.command.admin;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;

public final class SubCommandAdmin extends CombatLogCommand {
    public SubCommandAdmin(@NotNull NewbieHelperExpansion expansion) {
        super(expansion.getPlugin(), "admin");
        setPermissionName("combatlogx.command.togglepvp.admin");
        addSubCommand(new SubCommandAdminOn(expansion));
        addSubCommand(new SubCommandAdminOff(expansion));
    }
}

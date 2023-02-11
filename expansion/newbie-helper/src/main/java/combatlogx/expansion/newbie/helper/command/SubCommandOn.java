package combatlogx.expansion.newbie.helper.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;

public final class SubCommandOn extends CombatLogPlayerCommand {
    private final NewbieHelperExpansion expansion;

    public SubCommandOn(NewbieHelperExpansion expansion) {
        super(expansion.getPlugin(), "on");
        setPermissionName("combatlogx.command.togglepvp");
        this.expansion = expansion;
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        NewbieHelperExpansion expansion = getExpansion();
        if (expansion.shouldCheckDisabledWorlds() && isWorldDisabled(player)) {
            sendMessageWithPrefix(player, "error.disabled-world");
            return true;
        }

        PVPManager pvpManager = expansion.getPVPManager();
        pvpManager.setPVP(player, true);
        pvpManager.sendToggleMessage(player);
        return true;
    }

    private NewbieHelperExpansion getExpansion() {
        return this.expansion;
    }
}

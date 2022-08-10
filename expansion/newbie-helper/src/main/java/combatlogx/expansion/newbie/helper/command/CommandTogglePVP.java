package combatlogx.expansion.newbie.helper.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.command.admin.SubCommandAdmin;
import combatlogx.expansion.newbie.helper.manager.CooldownManager;
import combatlogx.expansion.newbie.helper.manager.PVPManager;

public final class CommandTogglePVP extends CombatLogPlayerCommand {
    private final NewbieHelperExpansion expansion;

    public CommandTogglePVP(NewbieHelperExpansion expansion) {
        super(expansion.getPlugin(), "togglepvp");
        this.expansion = expansion;

        setPermissionName("combatlogx.command.togglepvp");
        addSubCommand(new SubCommandAdmin(expansion));
        addSubCommand(new SubCommandCheck(expansion));
        addSubCommand(new SubCommandOn(expansion));
        addSubCommand(new SubCommandOff(expansion));
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean execute(Player player, String[] args) {
        NewbieHelperExpansion expansion = getExpansion();
        if (expansion.shouldCheckDisabledWorlds() && isWorldDisabled(player)) {
            sendMessageWithPrefix(player, "error.disabled-world", null);
            return true;
        }

        CooldownManager cooldownManager = expansion.getCooldownManager();
        if (cooldownManager.hasCooldown(player)) {
            cooldownManager.sendCooldownMessage(player);
            return true;
        } else {
            cooldownManager.addCooldown(player);
        }

        PVPManager pvpManager = expansion.getPVPManager();
        boolean pvpDisabled = pvpManager.isDisabled(player);

        pvpManager.setPVP(player, pvpDisabled);
        pvpManager.sendToggleMessage(player);
        return true;
    }

    private NewbieHelperExpansion getExpansion() {
        return this.expansion;
    }
}

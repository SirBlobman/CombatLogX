package combatlogx.expansion.newbie.helper.command;

import org.jetbrains.annotations.NotNull;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.command.admin.SubCommandAdmin;
import combatlogx.expansion.newbie.helper.configuration.NewbieHelperConfiguration;
import combatlogx.expansion.newbie.helper.manager.CooldownManager;
import combatlogx.expansion.newbie.helper.manager.PVPManager;

public final class CommandTogglePVP extends CombatLogPlayerCommand {
    private final NewbieHelperExpansion expansion;

    public CommandTogglePVP(@NotNull NewbieHelperExpansion expansion) {
        super(expansion.getPlugin(), "togglepvp");
        setPermissionName("combatlogx.command.togglepvp");
        addSubCommand(new SubCommandAdmin(expansion));
        addSubCommand(new SubCommandCheck(expansion));
        addSubCommand(new SubCommandOn(expansion));
        addSubCommand(new SubCommandOff(expansion));
        this.expansion = expansion;
    }

    @Override
    public boolean execute(@NotNull Player player, String @NotNull [] args) {
        NewbieHelperExpansion expansion = getExpansion();
        NewbieHelperConfiguration configuration = expansion.getConfiguration();

        World world = player.getWorld();
        if (configuration.isPreventPvpToggleInDisabledWorlds() && isWorldDisabled(world)) {
            sendMessageWithPrefix(player, "error.disabled-world");
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

    private @NotNull NewbieHelperExpansion getExpansion() {
        return this.expansion;
    }
}

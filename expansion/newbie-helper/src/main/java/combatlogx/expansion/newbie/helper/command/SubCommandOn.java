package combatlogx.expansion.newbie.helper.command;

import org.jetbrains.annotations.NotNull;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.configuration.NewbieHelperConfiguration;
import combatlogx.expansion.newbie.helper.manager.PVPManager;

public final class SubCommandOn extends CombatLogPlayerCommand {
    private final NewbieHelperExpansion expansion;

    public SubCommandOn(@NotNull NewbieHelperExpansion expansion) {
        super(expansion.getPlugin(), "on");
        setPermissionName("combatlogx.command.togglepvp");
        this.expansion = expansion;
    }

    @Override
    protected boolean execute(@NotNull Player player, String @NotNull [] args) {
        NewbieHelperExpansion expansion = getExpansion();
        NewbieHelperConfiguration configuration = expansion.getConfiguration();

        World world = player.getWorld();
        if (configuration.isPreventPvpToggleInDisabledWorlds() && isWorldDisabled(world)) {
            sendMessageWithPrefix(player, "error.disabled-world");
            return true;
        }

        PVPManager pvpManager = expansion.getPVPManager();
        pvpManager.setPVP(player, true);
        pvpManager.sendToggleMessage(player);
        return true;
    }

    private @NotNull NewbieHelperExpansion getExpansion() {
        return this.expansion;
    }
}

package combatlogx.expansion.cheat.prevention;

import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.cheat.prevention.configuration.IBlockConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IBucketConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IChatConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.ICommandConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IEntityConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IFlightConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IGameModeConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IInventoryConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IItemConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IPotionConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.ITeleportConfiguration;
import org.jetbrains.annotations.NotNull;

public interface ICheatPreventionExpansion {
    @NotNull Expansion getExpansion();
    @NotNull IConfiguration getConfiguration();
    @NotNull IBlockConfiguration getBlockConfiguration();
    @NotNull IBucketConfiguration getBucketConfiguration();
    @NotNull IChatConfiguration getChatConfiguration();
    @NotNull ICommandConfiguration getCommandConfiguration();
    @NotNull IEntityConfiguration getEntityConfiguration();
    @NotNull IFlightConfiguration getFlightConfiguration();
    @NotNull IGameModeConfiguration getGameModeConfiguration();
    @NotNull IInventoryConfiguration getInventoryConfiguration();
    @NotNull IItemConfiguration getItemConfiguration();
    @NotNull IPotionConfiguration getPotionConfiguration();
    @NotNull ITeleportConfiguration getTeleportConfiguration();
}

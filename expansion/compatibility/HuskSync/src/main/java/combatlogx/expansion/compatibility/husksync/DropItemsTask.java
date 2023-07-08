package combatlogx.expansion.compatibility.husksync;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.github.sirblobman.api.folia.details.LocationTaskDetails;
import com.github.sirblobman.api.utility.ItemUtility;

public final class DropItemsTask extends LocationTaskDetails {
    private final Collection<ItemStack> dropList;

    public DropItemsTask(@NotNull Plugin plugin, @NotNull Location location, @NotNull Collection<ItemStack> drops) {
        super(plugin, location);
        this.dropList = drops;
        setDelay(1L);
    }

    @Override
    public void run() {
        Location location = getLocation();
        World world = location.getWorld();
        for (ItemStack stack : this.dropList) {
            if (ItemUtility.isAir(stack)) {
                continue;
            }

            world.dropItemNaturally(location, stack);
        }
    }
}

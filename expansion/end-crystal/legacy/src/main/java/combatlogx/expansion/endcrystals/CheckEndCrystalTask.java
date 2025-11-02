package combatlogx.expansion.endcrystals;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.details.LocationTaskDetails;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICrystalManager;
import com.github.sirblobman.api.shaded.xseries.XEntityType;

public final class CheckEndCrystalTask extends LocationTaskDetails {
    private final ICombatLogX plugin;
    private final Player player;

    public CheckEndCrystalTask(@NotNull ICombatLogX plugin, @NotNull Location location, @NotNull Player player) {
        super(plugin.getPlugin(), location);
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        Location location = getLocation();
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        Collection<Entity> nearbyEntityCollection = world.getNearbyEntities(location, 4.0D, 4.0D, 4.0D);
        for (Entity entity : nearbyEntityCollection) {
            XEntityType entityType = XEntityType.of(entity);
            if (entityType != XEntityType.END_CRYSTAL) {
                continue;
            }

            ICombatLogX combatLogX = getCombatLogX();
            ICrystalManager crystalManager = combatLogX.getCrystalManager();
            crystalManager.setPlacer(entity, getPlayer());
            break;
        }
    }

    private @NotNull ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    private @NotNull Player getPlayer() {
        return this.player;
    }
}

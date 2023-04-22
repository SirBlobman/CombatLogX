package combatlogx.expansion.endcrystals;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.ICrystalManager;

public final class ListenerCrystals_Legacy extends ExpansionListener {
    public ListenerCrystals_Legacy(@NotNull Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = e.getClickedBlock();
        if (block == null) {
            return;
        }

        // End Crystals can only be placed on obsidian/bedrock.
        // If there is another way, this check may be changed.
        Material blockType = block.getType();
        if (blockType != Material.OBSIDIAN && blockType != Material.BEDROCK) {
            return;
        }

        ItemStack item = e.getItem();
        if (ItemUtility.isAir(item)) {
            return;
        }

        Material itemType = item.getType();
        if (itemType != Material.END_CRYSTAL) {
            return;
        }

        Player player = e.getPlayer();
        Runnable task = () -> checkEndCrystal(block, player);

        JavaPlugin javaPlugin = getJavaPlugin();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(javaPlugin, task, 1L);
    }

    private void checkEndCrystal(@NotNull Block block, @NotNull Player player) {
        Location location = block.getLocation();
        World world = block.getWorld();

        Collection<Entity> nearbyEntityCollection = world.getNearbyEntities(location, 4.0D, 4.0D, 4.0D);
        for (Entity entity : nearbyEntityCollection) {
            EntityType entityType = entity.getType();
            if (entityType != EntityType.ENDER_CRYSTAL) {
                continue;
            }

            ICombatLogX combatLogX = getCombatLogX();
            ICrystalManager crystalManager = combatLogX.getCrystalManager();
            crystalManager.setPlacer(entity, player);
            break;
        }
    }
}

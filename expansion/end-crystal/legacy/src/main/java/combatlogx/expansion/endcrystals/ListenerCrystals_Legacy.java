package combatlogx.expansion.endcrystals;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

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
        CheckEndCrystalTask task = new CheckEndCrystalTask(getCombatLogX(), block.getLocation(), player);
        task.setDelay(1L);

        TaskScheduler<ConfigurablePlugin> scheduler = getCombatLogX().getFoliaHelper().getScheduler();
        scheduler.scheduleLocationTask(task);
    }
}

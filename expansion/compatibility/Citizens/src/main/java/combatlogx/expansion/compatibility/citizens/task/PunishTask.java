package combatlogx.expansion.compatibility.citizens.task;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.github.sirblobman.api.folia.FoliaHelper;
import com.github.sirblobman.api.folia.details.EntityTaskDetails;
import com.github.sirblobman.api.folia.teleport.TeleportHandler;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.configuration.CitizensConfiguration;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.manager.InventoryManager;

public final class PunishTask extends EntityTaskDetails<Player> {
    /**
     * Player data flag: the stored inventory must be restored after the player's next respawn.
     */
    public static final String RESTORE_AFTER_RESPAWN_PATH = "citizens-compatibility.restore-after-respawn";

    private final CitizensExpansion expansion;

    public PunishTask(@NotNull CitizensExpansion expansion, @NotNull Player entity) {
        super(expansion.getPlugin().getPlugin(), entity);
        setDelay(1L);

        this.expansion = expansion;
    }

    @Override
    public void run() {
        Player player = getEntity();
        if (player == null) {
            return;
        }

        punish(player);
        player.setCanPickupItems(true);
    }

    private @NotNull CitizensExpansion getExpansion() {
        return this.expansion;
    }

    private @NotNull CitizensConfiguration getCitizensConfiguration(@NotNull World world) {
        CitizensExpansion expansion = getExpansion();
        return expansion.getCitizensConfiguration(world);
    }

    private @NotNull CombatNpcManager getCombatNpcManager() {
        CitizensExpansion expansion = getExpansion();
        return expansion.getCombatNpcManager();
    }

    private @NotNull InventoryManager getInventoryManager() {
        CitizensExpansion expansion = getExpansion();
        return expansion.getInventoryManager();
    }

    private @NotNull ICombatLogX getCombatLogX() {
        CitizensExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private @NotNull ICombatManager getCombatManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getCombatManager();
    }

    private void punish(@NotNull Player player) {
        if (player.hasMetadata("NPC")) {
            return;
        }

        CombatNpcManager combatNpcManager = getCombatNpcManager();
        YamlConfiguration playerData = combatNpcManager.getData(player);
        if (!playerData.getBoolean("citizens-compatibility.punish")) {
            return;
        }

        playerData.set("citizens-compatibility.punish", false);
        combatNpcManager.saveData(player);

        // Use the settings of the world the NPC was in, not the world the player happens to spawn in.
        Location location = combatNpcManager.loadLocation(player);
        World world = (location == null || location.getWorld() == null ? player.getWorld() : location.getWorld());
        CitizensConfiguration configuration = getCitizensConfiguration(world);

        if (configuration.isStoreLocation()) {
            if (location != null) {
                FoliaHelper foliaHelper = getExpansion().getPlugin().getFoliaHelper();
                TeleportHandler teleporter = foliaHelper.getTeleporter();
                teleporter.teleport(player, location).join();
            }

            playerData.set("citizens-compatibility.location", null);
        }

        if (configuration.isStoreInventory()) {
            PlayerInventory playerInventory = player.getInventory();
            playerInventory.clear();
        }

        double health = combatNpcManager.loadHealth(player);
        setHealth(player, health);

        if (health <= 0.0D) {
            playerData.set("citizens-compatibility.inventory", null);
            playerData.set("citizens-compatibility.armor", null);
        }

        if (configuration.isStoreInventory()) {
            InventoryManager inventoryManager = getInventoryManager();
            if (health > 0.0D) {
                inventoryManager.restoreInventory(player);
            } else if (inventoryManager.getStoredInventory(player) != null) {
                // The player was just killed. Items given to a dead player are wiped on respawn,
                // so restore the kept inventory (death-inventory: KEEP) after the respawn instead.
                playerData.set(RESTORE_AFTER_RESPAWN_PATH, true);
                combatNpcManager.saveData(player);
            }
        }

        if (configuration.isTagPlayer() && health > 0.0D) {
            ICombatManager combatManager = getCombatManager();
            combatManager.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
        }
    }

    private void setHealth(@NotNull Player player, double health) {
        if (Double.isInfinite(health) || Double.isNaN(health)) {
            health = 0.0D;
        }

        ICombatLogX combatLogX = getCombatLogX();
        MultiVersionHandler multiVersionHandler = combatLogX.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();

        double maxHealth = entityHandler.getMaxHealth(player);
        if (maxHealth < health) {
            entityHandler.setMaxHealth(player, health);
        }

        player.setHealth(health);
    }
}

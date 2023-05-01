package combatlogx.expansion.compatibility.citizens.task;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.PlayerInventory;

import com.github.sirblobman.api.folia.details.EntityTaskDetails;
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

    private @NotNull CitizensConfiguration getCitizensConfiguration() {
        CitizensExpansion expansion = getExpansion();
        return expansion.getCitizensConfiguration();
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

        CitizensConfiguration configuration = getCitizensConfiguration();
        if (configuration.isStoreLocation()) {
            Location location = combatNpcManager.loadLocation(player);
            if (location != null) {
                player.teleport(location, TeleportCause.PLUGIN);
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
            inventoryManager.restoreInventory(player);
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

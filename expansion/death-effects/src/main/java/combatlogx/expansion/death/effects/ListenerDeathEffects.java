package combatlogx.expansion.death.effects;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Spigot;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.api.item.ItemBuilder;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;
import com.github.sirblobman.api.shaded.xseries.XMaterial;

import combatlogx.expansion.death.effects.task.ItemRemoveTask;

public final class ListenerDeathEffects extends ExpansionListener {
    private final DeathEffectsExpansion expansion;

    public ListenerDeathEffects(DeathEffectsExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        DeathEffectsConfiguration configuration = getConfiguration();
        if (configuration.isRequireCombatDeath()) {
            ICombatLogX combatLogX = getCombatLogX();
            IDeathManager deathManager = combatLogX.getDeathManager();
            if (!deathManager.wasPunishKilled(player)) {
                return;
            }
        }

        if (configuration.hasEffect("BLOOD")) {
            playBloodEffect(player);
        }

        if (configuration.hasEffect("LIGHTNING")) {
            playLightningEffect(player);
        }

        if (configuration.hasEffect("BLOOD_ITEMS")) {
            playBloodItemsEffect(player);
        }
    }

    private DeathEffectsExpansion getDeathEffectsExpansion() {
        return this.expansion;
    }

    private DeathEffectsConfiguration getConfiguration() {
        DeathEffectsExpansion expansion = getDeathEffectsExpansion();
        return expansion.getConfiguration();
    }

    private void playLightningEffect(Player player) {
        DeathEffectsConfiguration configuration = getConfiguration();
        boolean effectOnly = configuration.isLightningEffectOnly();
        boolean silent = configuration.isLightningSilent();

        Location location = player.getLocation();
        World world = player.getWorld();
        Spigot spigot = world.spigot();

        if (effectOnly) {
            spigot.strikeLightningEffect(location, silent);
        } else {
            spigot.strikeLightning(location, silent);
        }
    }

    private void playBloodEffect(Player player) {
        Material bukkitMaterial = XMaterial.REDSTONE_BLOCK.get();
        if (bukkitMaterial != null) {
            World world = player.getWorld();
            Location location = player.getLocation();
            world.playEffect(location, Effect.STEP_SOUND, bukkitMaterial);
        }

        Location location = player.getLocation();
        List<Entity> nearbyEntityList = player.getNearbyEntities(200D, 20.0D, 20.0D);
        for (Entity entity : nearbyEntityList) {
            if (entity instanceof Player other) {
                sendFakeRedstoneDust(other, location);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void sendFakeRedstoneDust(Player player, Location location) {
        Material bukkitMaterial = XMaterial.REDSTONE_WIRE.parseMaterial();
        if (bukkitMaterial == null) {
            return;
        }

        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 13) {
            player.sendBlockChange(location, bukkitMaterial, (byte) 0);
            return;
        }

        BlockData blockData = bukkitMaterial.createBlockData();
        player.sendBlockChange(location, blockData);
    }

    private void playBloodItemsEffect(Player player) {
        ICombatLogX combatLogX = getCombatLogX();
        MultiVersionHandler multiVersionHandler = combatLogX.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        Location location = player.getLocation();

        DeathEffectsConfiguration configuration = getConfiguration();
        int amount = configuration.getBloodItemsAmount();
        for (int i = 0; i < amount; i++) {
            spawnFakeItem(location, entityHandler);
        }
    }

    private void spawnFakeItem(Location location, EntityHandler entityHandler) {
        DeathEffectsConfiguration configuration = getConfiguration();
        XMaterial material = configuration.getBloodItemsMaterial();
        ItemStack item = new ItemBuilder(material).build();

        Item itemEntity = entityHandler.spawnEntity(location, Item.class, preItem -> {
            preItem.setItemStack(item);
            preItem.setPickupDelay(Integer.MAX_VALUE);
        });

        long delay = configuration.getBloodItemsStayTicks();
        ItemRemoveTask removeTask = new ItemRemoveTask(getJavaPlugin(), itemEntity);
        removeTask.setDelay(delay);

        TaskScheduler scheduler = getCombatLogX().getFoliaHelper().getScheduler();
        scheduler.scheduleEntityTask(removeTask);
    }
}

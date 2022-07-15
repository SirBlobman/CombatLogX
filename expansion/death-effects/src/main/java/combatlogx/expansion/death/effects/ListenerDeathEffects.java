package combatlogx.expansion.death.effects;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Spigot;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.api.xseries.XMaterial;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;

public final class ListenerDeathEffects extends ExpansionListener {
    public ListenerDeathEffects(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        YamlConfiguration configuration = getConfiguration();
        List<String> enabledDeathEffectList = configuration.getStringList("death-effect-list");
        if (enabledDeathEffectList.isEmpty()) {
            return;
        }

        Player player = e.getEntity();
        boolean requireCombatDeath = configuration.getBoolean("combat-death-only");
        if (requireCombatDeath) {
            ICombatLogX combatLogX = getCombatLogX();
            IDeathManager deathManager = combatLogX.getDeathManager();
            if (!deathManager.wasPunishKilled(player)) {
                return;
            }
        }

        if (enabledDeathEffectList.contains("BLOOD")) {
            playBloodEffect(player);
        }

        if (enabledDeathEffectList.contains("LIGHTNING")) {
            playLightningEffect(player);
        }
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("config.yml");
    }

    private void playLightningEffect(Player player) {
        YamlConfiguration configuration = getConfiguration();
        boolean effectOnly = configuration.getBoolean("lightning.effect-only");
        boolean silent = configuration.getBoolean("lightning.silent");

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
        Material bukkitMaterial = XMaterial.REDSTONE_BLOCK.parseMaterial();
        if (bukkitMaterial != null) {
            World world = player.getWorld();
            Location location = player.getLocation();
            world.playEffect(location, Effect.STEP_SOUND, bukkitMaterial);
        }

        Location location = player.getLocation();
        List<Entity> nearbyEntityList = player.getNearbyEntities(200D, 20.0D, 20.0D);
        for (Entity entity : nearbyEntityList) {
            if (!(entity instanceof Player)) {
                continue;
            }

            Player other = (Player) entity;
            sendFakeRedstoneDust(other, location);
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
}

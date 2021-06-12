package combatlogx.expansion.force.field;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.object.WorldXYZ;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.api.xseries.XMaterial;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import com.comphenix.protocol.wrappers.WrappedBlockData;

/** @author olivolja3 */
public class ForceFieldAdapter extends PacketAdapter {
    private final ICombatLogX plugin;
    private final ListenerForceField forceFieldListener;
    public ForceFieldAdapter(ICombatLogX plugin, ListenerForceField forceFieldListener) {
        super(plugin.getPlugin(), ListenerPriority.NORMAL, Client.USE_ITEM, Client.BLOCK_DIG, Server.BLOCK_CHANGE);
        this.plugin = plugin;
        this.forceFieldListener = forceFieldListener;
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        if(e.isCancelled()) return;

        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        World world = player.getWorld();
        PacketContainer packet = e.getPacket();
        Location location = packet.getBlockPositionModifier().read(0).toLocation(world);

        if(isForceFieldBlock(player, location)) {
            PlayerDigType digType = packet.getPlayerDigTypes().read(0);
            GameMode gameMode = player.getGameMode();
            if(digType == PlayerDigType.STOP_DESTROY_BLOCK
                    || (digType == PlayerDigType.START_DESTROY_BLOCK && gameMode == GameMode.CREATIVE)) {
                this.forceFieldListener.sendForceField(player, location);
            }
        }
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        if(e.isCancelled()) return;

        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        World world = player.getWorld();
        PacketContainer packet = e.getPacket();
        Location location = packet.getBlockPositionModifier().read(0).toLocation(world);

        if(isForceFieldBlock(player, location)) {
            WrappedBlockData wrappedBlockData = getWrappedBlockData();
            if(wrappedBlockData != null) {
                packet.getBlockData().writeSafely(0, wrappedBlockData);
            }
        }
    }

    private boolean isForceFieldBlock(Player player, Location location) {
        UUID uuid = player.getUniqueId();
        if(this.forceFieldListener.fakeBlockMap.containsKey(uuid)) {
            boolean isSafe = this.forceFieldListener.isSafe(player, location);
            boolean isSafeSurround = this.forceFieldListener.isSafeSurround(player, location);
            boolean canPlace = this.forceFieldListener.canPlace(WorldXYZ.from(location));
            if(isSafe && isSafeSurround && canPlace) {
                WorldXYZ worldXYZ = WorldXYZ.from(location);
                return this.forceFieldListener.fakeBlockMap.get(uuid).contains(worldXYZ);
            }
        }

        return false;
    }

    private WrappedBlockData getWrappedBlockData() {
        XMaterial material = this.forceFieldListener.getForceFieldMaterial();
        Material bukkitMaterial = material.parseMaterial();
        if(bukkitMaterial == null) return null;

        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion < 13) {
            byte data = material.getData();
            return WrappedBlockData.createData(bukkitMaterial, data);
        }

        return WrappedBlockData.createData(bukkitMaterial);
    }
}

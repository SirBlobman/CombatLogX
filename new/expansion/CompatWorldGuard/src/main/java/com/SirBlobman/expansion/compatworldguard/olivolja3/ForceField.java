package com.SirBlobman.expansion.compatworldguard.olivolja3;

import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.WEST;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.expansion.compatworldguard.config.ConfigWG;
import com.SirBlobman.expansion.compatworldguard.packetwrapper.WrapperPlayClientBlockDig;
import com.SirBlobman.expansion.compatworldguard.packetwrapper.WrapperPlayServerBlockChange;
import com.SirBlobman.expansion.compatworldguard.utility.WGUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import com.comphenix.protocol.wrappers.WrappedBlockData;

public final class ForceField implements Listener {

	private static final Plugin plugin = JavaPlugin.getPlugin(CombatLogX.class);
	private static final List<BlockFace> ALL_DIRECTIONS = Arrays.asList(NORTH, EAST, SOUTH, WEST);
	private static final int radius = ConfigWG.FORCEFIELD_SIZE;
	private static boolean enabled = ConfigWG.FORCEFIELD_ENABLED;
	private static Material material = ConfigWG.FORCEFIELD_MATERIAL;

	private static Map<UUID, Set<Location>> previousUpdates = new HashMap<>();

	private boolean isSafeZone(Location loc) {
		return WGUtil.allowsPvP(loc) && WGUtil.allowsMobCombat(loc);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void updateViewedBlocks(PlayerMoveEvent e) {
		if (!enabled)
			return;
		Player p = e.getPlayer();
		if (!CombatUtil.isInCombat(p))
			return;
		if (!isSafeZone(e.getTo()))
			return;
		Location t = e.getTo();
		Location f = e.getFrom();
		if (t.getBlockX() == f.getBlockX() && t.getBlockY() == f.getBlockY() && t.getBlockZ() == f.getBlockZ())
			return;
		Set<Location> changedBlocks = getChangedBlocks(p);
		Set<Location> removeBlocks = new HashSet<>();
		if (previousUpdates.containsKey(p.getUniqueId()))
			removeBlocks = previousUpdates.get(p.getUniqueId());
		for (Location location : changedBlocks) {
			p.sendBlockChange(location,
					plugin.getServer().createBlockData(material));
			removeBlocks.remove(location);
		}
		for (Location location : removeBlocks) {
			Block block = location.getBlock();
			if (block.getType().equals(Material.WATER) || block.getType().equals(Material.LAVA)) p.sendBlockChange(location, block.getBlockData());
			else p.sendBlockChange(location, plugin.getServer().createBlockData(Material.BEDROCK));
		}
		previousUpdates.put(p.getUniqueId(), changedBlocks);
	}

	@EventHandler
	public void pue(PlayerUntagEvent e) {
		Player p = e.getPlayer();
		Set<Location> removeBlocks;
		if (previousUpdates.containsKey(p.getUniqueId())) {
			removeBlocks = previousUpdates.get(p.getUniqueId());
		} else {
			removeBlocks = new HashSet<>();
		}
		for (Location location : removeBlocks) {
			Block block = location.getBlock();
			if (block.getType().equals(Material.WATER) || block.getType().equals(Material.LAVA)) p.sendBlockChange(location, block.getBlockData());
			else p.sendBlockChange(location, plugin.getServer().createBlockData(Material.BEDROCK));
		}
		previousUpdates.remove(p.getUniqueId());
	}

	private Set<Location> getChangedBlocks(Player p) {
		Set<Location> locations = new HashSet<>();

		if (!CombatUtil.isInCombat(p))
			return locations;

		Location l = p.getLocation();
		Location loc1 = l.clone().add(radius, 0, radius);
		Location loc2 = l.clone().subtract(radius, 0, radius);
		int topBlockX = loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
		int bottomBlockX = loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
		int topBlockZ = loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();
		int bottomBlockZ = loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();

		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				Location location = new Location(l.getWorld(), (double) x, l.getY(), (double) z);
				if (isSafeZone(location))
					continue;
				if (!isPvpSurrounding(location))
					continue;
				for (int i = -radius; i < radius; i++) {
					Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
					loc.setY(loc.getY() + i);
					if (!(loc.getBlock().getType().equals(Material.AIR)
							|| loc.getBlock().getType().equals(Material.WATER)
							|| loc.getBlock().getType().equals(Material.LAVA)))
						continue;
					locations.add(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
				}
			}
		}
		return locations;
	}

	private boolean isPvpSurrounding(Location loc) {
		for (BlockFace direction : ALL_DIRECTIONS) {
			if (isSafeZone(loc.getBlock().getRelative(direction).getLocation())) {
				return true;
			}
		}
		return false;
	}

	public static void registerProtocol() {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.BLOCK_CHANGE) {
			@Override
			public void onPacketSending(PacketEvent e) {
				if (e.isCancelled()) return;
				if (e.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
					WrapperPlayServerBlockChange block = new WrapperPlayServerBlockChange(e.getPacket());
					BlockPosition bLoc = block.getLocation();
					Location loc = new Location(e.getPlayer().getWorld(), bLoc.getX(), bLoc.getY(), bLoc.getZ());
					if (previousUpdates.containsKey(e.getPlayer().getUniqueId()) &&
							previousUpdates.get(e.getPlayer().getUniqueId()).contains(loc)) {
						WrappedBlockData bData = block.getBlockData();
						if (bData.getType() == Material.AIR) {
							bData.setType(material);
							block.setBlockData(bData);
						} else if (bData.getType() == Material.BEDROCK) {
							bData.setType(Material.AIR);
							block.setBlockData(bData);
						}
					}
				}
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
			@Override
			public void onPacketReceiving(PacketEvent e) {
				if (e.isCancelled()) return;
				if (e.getPacketType() == PacketType.Play.Client.BLOCK_DIG) {
					WrapperPlayClientBlockDig block = new WrapperPlayClientBlockDig(e.getPacket());
					BlockPosition bLoc = block.getLocation();
					Location loc = new Location(e.getPlayer().getWorld(), bLoc.getX(), bLoc.getY(), bLoc.getZ());
					if (block.getStatus() == PlayerDigType.STOP_DESTROY_BLOCK &&
							previousUpdates.containsKey(e.getPlayer().getUniqueId()) &&
							previousUpdates.get(e.getPlayer().getUniqueId()).contains(loc)) {
						e.getPlayer().sendBlockChange(loc, plugin.getServer().createBlockData(Material.AIR));
					}
				}
			}
		});
	}

}

package com.github.sirblobman.combatlogx.force.field;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.object.WorldXYZ;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

/** @author olivolja3 */
public class ForceFieldAdapter extends PacketAdapter {
    private final ICombatLogX plugin;
    private final ListenerForceField forceFieldListener;
    public ForceFieldAdapter(ListenerForceField forceFieldListener) {
        super(forceFieldListener.getPlugin().getPlugin(), ListenerPriority.NORMAL, Server.BLOCK_CHANGE);
        this.forceFieldListener = forceFieldListener;
        this.plugin = forceFieldListener.getPlugin();
    }

    public ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        if(e.isCancelled()) return;
        Player player = e.getPlayer();

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        UUID uuid = player.getUniqueId();
        World world = player.getWorld();
        PacketContainer packetContainer = e.getPacket();

        WrapperPlayServerBlockChange block = new WrapperPlayServerBlockChange(packetContainer);
        Location location = packetContainer.getBlockPositionModifier().read(0).toLocation(world);

        if(this.forceFieldListener.fakeBlockMap.containsKey(uuid)
                && this.forceFieldListener.isSafe(player, location)
                && this.forceFieldListener.isSafeSurround(location, player)
                && this.forceFieldListener.canPlace(location)
                && this.forceFieldListener.fakeBlockMap.get(uuid).contains(WorldXYZ.from(location))
                ) {
            block.setBlockData(this.forceFieldListener.wrapData(block.getBlockData()));
        }
    }
}

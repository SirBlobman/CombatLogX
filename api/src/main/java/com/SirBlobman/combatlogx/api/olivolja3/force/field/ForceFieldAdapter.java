package com.SirBlobman.combatlogx.api.olivolja3.force.field;

import java.util.UUID;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class ForceFieldAdapter extends PacketAdapter {
    private final ForceField forceField;
    private final ICombatLogX plugin;
    ForceFieldAdapter(ForceField forceField) {
        super(forceField.getExpansion().getPlugin().getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Server.BLOCK_CHANGE);
        this.forceField = forceField;
        this.plugin = this.forceField.getExpansion().getPlugin();
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        if(e.isCancelled()) return;

        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        UUID uuid = player.getUniqueId();
        PacketContainer packet = e.getPacket();
        World world = player.getWorld();

        WrapperPlayServerBlockChange block = new WrapperPlayServerBlockChange(packet);
        Location location = packet.getBlockPositionModifier().read(0).toLocation(world);
        if(this.forceField.fakeBlocks.containsKey(uuid) && this.forceField.isSafe(location, player) && this.forceField.isSafeSurround(location, player) && ForceField.canPlace(location) && this.forceField.fakeBlocks.get(uuid).contains(location)) {
            block.setBlockData(this.forceField.wrappedData(block.getBlockData()));
        }
    }
}
package com.SirBlobman.combatlogx.olivolja3.force.field;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class ForceFieldAdapter extends PacketAdapter {
    private static final CombatLogX plugin = JavaPlugin.getPlugin(CombatLogX.class);
    private final ForceField forceField;
    public ForceFieldAdapter(ForceField forceField) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.BLOCK_CHANGE);
        this.forceField = forceField;
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        if(e.isCancelled()) return;

        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;

        UUID uuid = player.getUniqueId();
        PacketContainer packet = e.getPacket();
        player.getWorld()

        WrapperPlayServerBlockChange block = new WrapperPlayServerBlockChange(packet);
        Location location = packet.getBlockPositionModifier().read(0).toLocation(player.getWorld());
        if(this.forceField.fakeBlocks.containsKey(uuid) && this.forceField.isSafe(location, player) && this.forceField.isSafeSurround(location, player) && this.forceField.canPlace(location) && this.forceField.fakeBlocks.get(uuid).contains(location)) {
            block.setBlockData(this.forceField.wrappedData(block.getBlockData()));
        }
    }
}
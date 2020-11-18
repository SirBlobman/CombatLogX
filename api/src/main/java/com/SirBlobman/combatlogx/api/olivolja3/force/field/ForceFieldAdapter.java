package com.SirBlobman.combatlogx.api.olivolja3.force.field;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ForceFieldAdapter extends PacketAdapter {
    private final ForceField forceField;
    private final ICombatLogX plugin;

    ForceFieldAdapter(ForceField forceField) {
        super(forceField.getExpansion().getPlugin().getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Client.USE_ITEM, PacketType.Play.Client.BLOCK_DIG);
        this.forceField = forceField;
        this.plugin = this.forceField.getExpansion().getPlugin();
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        if(e.isCancelled()) return;

        Player player = e.getPlayer();
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        UUID uuid = player.getUniqueId();
        PacketContainer packet = e.getPacket();
        World world = player.getWorld();
        Location location = packet.getBlockPositionModifier().read(0).toLocation(world);

        if(this.forceField.fakeBlockMap.containsKey(uuid) && this.forceField.isSafe(location, player) && this.forceField.isSafeSurround(location, player) && ForceField.canPlace(location) && this.forceField.fakeBlockMap.get(uuid).contains(location)) {
            if(e.getPacketType() == PacketType.Play.Client.BLOCK_DIG) {
                EnumWrappers.PlayerDigType type = packet.getPlayerDigTypes().read(0);
                if(type == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK || (player.getGameMode() == GameMode.CREATIVE && type == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK))
                    this.forceField.sendForceField(player, location);
            }

            //Possible fix for false flags with some AntiCheats
            e.setCancelled(true);
        }
    }
}
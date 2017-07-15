package com.SirBlobman.combatlogx.nms.action;

import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_11_R1.PacketPlayOutChat;
import net.minecraft.server.v1_11_R1.PlayerConnection;

public class Action1_11_R1 implements Action {
	@Override
	public void action(Player p, String msg) {
		String json = Util.json(msg);
		IChatBaseComponent icbc = ChatSerializer.a(json);
		PacketPlayOutChat ppoc = new PacketPlayOutChat(icbc, (byte) 2);
		CraftPlayer cp = (CraftPlayer) p;
		EntityPlayer ep = cp.getHandle();
		PlayerConnection pc = ep.playerConnection;
		pc.sendPacket(ppoc);
	}
}

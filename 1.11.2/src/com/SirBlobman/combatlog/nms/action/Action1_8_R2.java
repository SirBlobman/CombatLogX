package com.SirBlobman.combatlog.nms.action;

import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlog.utility.Util;

import net.minecraft.server.v1_8_R2.PlayerConnection;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R2.PacketPlayOutChat;

public class Action1_8_R2 implements Action {
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

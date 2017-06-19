package com.SirBlobman.combatlog.nms.action;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlog.utility.Util;

import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;

public class Action1_12_R1 implements Action {
	@Override
	public void action(Player p, String msg) {
		String json = Util.json(msg);
		IChatBaseComponent icbc = ChatSerializer.a(json);
		ChatMessageType ACTION = ChatMessageType.GAME_INFO;
		PacketPlayOutChat ppoc = new PacketPlayOutChat(icbc, ACTION);
		CraftPlayer cp = (CraftPlayer) p;
		EntityPlayer ep = cp.getHandle();
		PlayerConnection pc = ep.playerConnection;
		pc.sendPacket(ppoc);
	}
}
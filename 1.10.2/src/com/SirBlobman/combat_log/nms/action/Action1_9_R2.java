package com.SirBlobman.combat_log.nms.action;

import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.IChatBaseComponent;
import net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_9_R2.PacketPlayOutChat;
import net.minecraft.server.v1_9_R2.PlayerConnection;

public class Action1_9_R2 implements Action
{
	@Override
	public void action(Player p, String msg)
	{
		String s = "{\"text\": \"" + msg + "\"}";
		IChatBaseComponent icbc = ChatSerializer.a(s);
		PacketPlayOutChat ppoc = new PacketPlayOutChat(icbc, (byte) 2);
		CraftPlayer cp = (CraftPlayer) p;
		EntityPlayer ep = cp.getHandle();
		PlayerConnection pc = ep.playerConnection;
		pc.sendPacket(ppoc);
	}
}
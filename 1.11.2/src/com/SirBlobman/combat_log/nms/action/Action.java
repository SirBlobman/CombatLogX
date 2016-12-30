package com.SirBlobman.combat_log.nms.action;

import org.bukkit.entity.Player;

public interface Action
{
	public void action(Player p, String msg);
}
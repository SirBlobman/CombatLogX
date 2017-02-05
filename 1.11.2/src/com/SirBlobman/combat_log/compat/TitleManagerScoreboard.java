package com.SirBlobman.combat_log.compat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;

public class TitleManagerScoreboard
{
	private TitleManagerAPI titleAPI;
	public TitleManagerScoreboard(Plugin api) {
		this.titleAPI = (TitleManagerAPI) api;
	}
	
	public void custom(Player p) {
		titleAPI.removeScoreboard(p);
	}
	
	public void reset(Player p) {
		titleAPI.giveScoreboard(p);
	}
}
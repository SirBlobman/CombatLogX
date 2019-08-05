package com.SirBlobman.expansion.cheatprevention.utility;

import java.util.List;

import com.SirBlobman.combatlogx.utility.Util;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CommandAlias;

public class CMIUtil {
	public static List<String> getAliases(String command) {
		if(command == null || command.isEmpty()) return Util.newList();
		CommandAlias alias = CMI.getInstance().getAliasManager().getAliasForCommand(command);
		if(alias == null) return Util.newList();
		
		return Util.newList(alias.getCommand().replace(" $1-", ""));
	}
}
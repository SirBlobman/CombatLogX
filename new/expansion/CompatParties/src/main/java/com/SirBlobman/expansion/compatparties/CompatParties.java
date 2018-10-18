package com.SirBlobman.expansion.compatparties;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;

public class CompatParties implements CLXExpansion, Listener {
	public String getUnlocalizedName() {return "CompatParties";}
	public String getName() {return "Parties Compatibility";}
	public String getVersion() {return "13.1";}
	
	@Override
	public void enable() {
		if(PluginUtil.isEnabled("Parties")) {
			PluginUtil.regEvents(this);
		} else {
			String error = "Parties is not installed, automatically disabling...";
			print(error);
			Expansions.unloadExpansion(this);
		}
	}
	
	@Override
	public void disable() {
		
	}
	
	@Override
	public void onConfigReload() {
		
	}
	
	@EventHandler(ignoreCancelled=true)
	public void beforeTag(PlayerPreTagEvent e) {
		Player p = e.getPlayer();
		LivingEntity enemy = e.getEnemy();
		if(enemy instanceof Player) {
			Player pe = (Player) enemy;

			PartiesAPI api = Parties.getApi();
			PartyPlayer pp = api.getPartyPlayer(p.getUniqueId());
			String partyName = pp.getPartyName();
			Party party = api.getParty(partyName);

			if(party != null) {
				UUID euuid = pe.getUniqueId();
				List<UUID> members = party.getMembers();
				if(members.contains(euuid)) e.setCancelled(true);
			}
		}
	}
}
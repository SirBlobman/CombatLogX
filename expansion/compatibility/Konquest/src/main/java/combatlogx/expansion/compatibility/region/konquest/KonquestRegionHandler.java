package combatlogx.expansion.compatibility.region.konquest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;

import konquest.api.KonquestAPI;
import konquest.api.manager.KonquestKingdomManager;
import konquest.api.model.KonquestKingdom;
import konquest.api.model.KonquestTerritory;

public class KonquestRegionHandler extends RegionHandler {
    public KonquestRegionHandler(KonquestExpansion expansion) {
        super(expansion);
    }

    @Override
    public String getEntryDeniedMessagePath(TagType tagType) {
        return "expansion.region-protection.konquest-no-entry";
    }

    @Override
    public boolean isSafeZone(Player player, Location location, TagType tagType) {
        KonquestAPI api = getAPI();
        if (api == null) {
            return false;
        }

        KonquestKingdomManager kingdomManager = api.getKingdomManager();
        if (kingdomManager == null) {
            return false;
        }

        KonquestTerritory territory = kingdomManager.getChunkTerritory(location);
        if (territory == null) {
            return false;
        }

        KonquestKingdom kingdom = territory.getKingdom();
        return (kingdom != null);
    }

    private KonquestAPI getAPI() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (!pluginManager.isPluginEnabled("Konquest")) {
            return null;
        }

        ServicesManager servicesManager = Bukkit.getServicesManager();
        RegisteredServiceProvider<KonquestAPI> registration = servicesManager.getRegistration(KonquestAPI.class);
        if (registration == null) {
            return null;
        }

        return registration.getProvider();
    }
}

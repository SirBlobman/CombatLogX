package combatlogx.expansion.cheat.prevention.listener;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.xseries.XMaterial;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class ListenerBuckets extends CheatPreventionListener {
    public ListenerBuckets(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        Player player = e.getPlayer();
        Material bukkitMaterial = e.getBucket();
        XMaterial material = XMaterial.matchXMaterial(bukkitMaterial);

        if (isInCombat(player) && shouldPreventEmpty(material)) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.buckets.no-empty", null);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketFillEvent e) {
        Player player = e.getPlayer();
        Material bukkitMaterial = e.getBucket();
        XMaterial material = XMaterial.matchXMaterial(bukkitMaterial);

        if (isInCombat(player) && shouldPreventFill(material)) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.buckets.no-fill", null);
        }
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("resources/buckets.yml");
    }

    private boolean shouldPreventEmpty(XMaterial material) {
        YamlConfiguration configuration = getConfiguration();
        if (!configuration.getBoolean("prevent-bucket-empty", false)) return false;

        String materialName = material.name();
        List<String> preventList = configuration.getStringList("prevent-bucket-empty-list");
        return preventList.contains(materialName);
    }

    private boolean shouldPreventFill(XMaterial material) {
        YamlConfiguration configuration = getConfiguration();
        if (!configuration.getBoolean("prevent-bucket-fill", false)) return false;

        String materialName = material.name();
        List<String> preventList = configuration.getStringList("prevent-bucket-fill-list");
        return preventList.contains(materialName);
    }
}

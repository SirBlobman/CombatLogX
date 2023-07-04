package combatlogx.expansion.cheat.prevention.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import com.github.sirblobman.api.shaded.xseries.XMaterial;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IBucketConfiguration;

public final class ListenerBuckets extends CheatPreventionListener {
    public ListenerBuckets(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        Player player = e.getPlayer();
        Material bukkitMaterial = e.getBucket();
        XMaterial material = XMaterial.matchXMaterial(bukkitMaterial);

        if (isInCombat(player) && isPreventEmpty(material)) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.buckets.no-empty");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketFillEvent e) {
        Player player = e.getPlayer();
        Material bukkitMaterial = e.getBucket();
        XMaterial material = XMaterial.matchXMaterial(bukkitMaterial);

        if (isInCombat(player) && isPreventFill(material)) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.buckets.no-fill");
        }
    }

    private @NotNull IBucketConfiguration getBucketConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getBucketConfiguration();
    }

    private boolean isPreventEmpty(@NotNull XMaterial material) {
        IBucketConfiguration configuration = getBucketConfiguration();
        return (configuration.isPreventBucketEmpty() && configuration.isPreventEmpty(material));
    }

    private boolean isPreventFill(@NotNull XMaterial material) {
        IBucketConfiguration configuration = getBucketConfiguration();
        return (configuration.isPreventBucketFill() && configuration.isPreventFill(material));
    }
}

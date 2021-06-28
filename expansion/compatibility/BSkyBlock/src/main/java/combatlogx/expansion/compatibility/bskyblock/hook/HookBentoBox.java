package combatlogx.expansion.compatibility.bskyblock.hook;

import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import combatlogx.expansion.compatibility.bskyblock.BSkyBlockExpansion;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.AddonDescription;
import world.bentobox.bentobox.managers.AddonsManager;

public final class HookBentoBox {
    public static boolean findBSkyBlock(BSkyBlockExpansion expansion) {
        Logger logger = expansion.getLogger();
        logger.info("Checking BentoBox for BSkyBlock...");

        BentoBox bentoBox = JavaPlugin.getPlugin(BentoBox.class);
        AddonsManager addonsManager = bentoBox.getAddonsManager();
        Optional<Addon> optionalAddon = addonsManager.getAddonByName("BSkyBlock");

        if(optionalAddon.isEmpty()) {
            logger.info("Failed to find BSkyBlock in BentoBox.");
            return false;
        }

        Addon addon = optionalAddon.get();
        AddonDescription description = addon.getDescription();
        String addonName = description.getName();
        String addonVersion = description.getVersion();

        String message = String.format(Locale.US, "Successfully found a dependency: %s v%s", addonName,
                addonVersion);
        logger.info(message);
        return true;
    }
}

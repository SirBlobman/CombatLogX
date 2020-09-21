package combatlogx.expansion.compatibility.bskyblock.hook;

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

        Addon addon = getBSkyBlock();
        if(addon == null) {
            logger.info("Failed to find BSkyBlock in BentoBox.");
            return false;
        }

        AddonDescription description = addon.getDescription();
        String version = description.getVersion();

        logger.info("Successfully found a dependency: BSkyBlock v" + version);
        return true;
    }

    public static Addon getBSkyBlock() {
        BentoBox bentoBox = JavaPlugin.getPlugin(BentoBox.class);
        AddonsManager addonsManager = bentoBox.getAddonsManager();
        Optional<Addon> optionalAddon = addonsManager.getAddonByName("BSkyBlock");
        return optionalAddon.orElse(null);
    }
}
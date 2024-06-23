package combatlogx.expansion.compatibility.znpc;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.znpc.configuration.Configuration;
import combatlogx.expansion.compatibility.znpc.configuration.NpcConfiguration;

public final class ZNPCExpansion extends Expansion {
    private final Configuration configuration;
    private final NpcConfiguration npcConfiguration;

    private final CombatNpcManager combatNpcManager;
    private final InventoryManager inventoryManager;

    public ZNPCExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);

        this.configuration = new Configuration();
        this.npcConfiguration = new NpcConfiguration(this);

        this.combatNpcManager = new CombatNpcManager(this);
        this.inventoryManager = new InventoryManager(this);
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
        configurationManager.saveDefault("npcs.yml");
    }

    @Override
    public void onEnable() {
        // Only ZNPCsPlus v2.0.0 is supported at this time.
        if(!checkDependency("ZNPCsPlus", true, "2")) {
            selfDisable();
            return;
        }

        reloadConfig();
        registerListeners();
    }

    @Override
    public void onDisable() {
        CombatNpcManager combatNpcManager = getCombatNpcManager();
        combatNpcManager.removeAll();
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        configurationManager.reload("npcs.yml");

        getConfiguration().load(configurationManager.get("config.yml"));
        getNpcConfiguration().load(configurationManager.get("npcs.yml"));
    }

    public @NotNull Configuration getConfiguration() {
        return this.configuration;
    }

    public @NotNull NpcConfiguration getNpcConfiguration() {
        return this.npcConfiguration;
    }

    public @NotNull CombatNpcManager getCombatNpcManager() {
        return this.combatNpcManager;
    }

    public @NotNull InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    private void registerListeners() {
        new ListenerCombat(this).register();
        new ListenerDeath(this).register();
        new ListenerJoin(this).register();
        new ListenerPunish(this).register();
        new ListenerQuit(this).register();

        // Totem of Undying was added in 1.11.
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion >= 11) {
            new ListenerResurrect(this).register();
        }

        // EntityTransformEvent was added in 1.13
        if (minorVersion >= 13) {
            new ListenerConvert(this).register();
        }
    }
}

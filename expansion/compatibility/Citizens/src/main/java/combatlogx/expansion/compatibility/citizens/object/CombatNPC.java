package combatlogx.expansion.compatibility.citizens.object;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import net.citizensnpcs.api.npc.NPC;

public final class CombatNPC extends BukkitRunnable {
    private final CitizensExpansion expansion;
    private final NPC originalNPC;
    private final UUID ownerId;
    private UUID enemyId;
    private long survivalTicks;

    public CombatNPC(CitizensExpansion expansion, NPC originalNPC, OfflinePlayer owner) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
        this.originalNPC = Validate.notNull(originalNPC, "originalNPC must not be null!");
        this.ownerId = Validate.notNull(owner, "owner must not be null!").getUniqueId();
    }

    @Override
    public void run() {
        this.survivalTicks--;
        if(this.survivalTicks > 0) return;

        YamlConfiguration configuration = this.expansion.getConfigurationManager().get("citizens.yml");
        if(configuration.getBoolean("stay-until-enemy-escape") && this.enemyId != null) {
            Player player = Bukkit.getPlayer(this.enemyId);
            ICombatManager combatManager = this.expansion.getPlugin().getCombatManager();
            if(player != null && combatManager.isInCombat(player)) {
                long timerLeftMillis = combatManager.getTimerLeftMillis(player);
                this.survivalTicks = (timerLeftMillis / 50L) + 1;
                return;
            }
        }

        CombatNpcManager combatNpcManager = this.expansion.getCombatNpcManager();
        combatNpcManager.remove(this);
    }

    public void start() {
        resetSurvivalTime();
        ICombatLogX combatLogX = this.expansion.getPlugin();
        JavaPlugin plugin = combatLogX.getPlugin();
        runTaskTimerAsynchronously(plugin, 1L, 1L);
    }

    public NPC getOriginalNPC() {
        return this.originalNPC;
    }

    public UUID getOwnerId() {
        return this.ownerId;
    }

    public OfflinePlayer getOfflineOwner() {
        UUID uuid = getOwnerId();
        return Bukkit.getOfflinePlayer(uuid);
    }

    public Player getOwner() {
        OfflinePlayer owner = getOfflineOwner();
        return owner.getPlayer();
    }

    public long getSurvivalTicksLeft() {
        return this.survivalTicks;
    }

    public void resetSurvivalTime() {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("citizens.yml");
        long survivalSeconds = configuration.getLong("survival-time");
        this.survivalTicks = (survivalSeconds * 20L);
    }

    public void setEnemy(Player enemy) {
        Validate.notNull(enemy, "enemy must not be null!");
        this.enemyId = enemy.getUniqueId();
    }
}

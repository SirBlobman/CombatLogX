package combatlogx.expansion.compatibility.znpc;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.details.TaskDetails;
import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

import combatlogx.expansion.compatibility.znpc.configuration.NpcConfiguration;
import lol.pyr.znpcsplus.api.npc.Npc;

public final class CombatNpc extends TaskDetails {
    private final ZNPCExpansion expansion;
    private final Npc originalNpc;
    private final UUID ownerId;

    private UUID enemyId;
    private long survivalTicks;

    public CombatNpc(@NotNull ZNPCExpansion expansion, @NotNull Npc originalNpc, @NotNull OfflinePlayer owner) {
        super(expansion.getPlugin().getPlugin());
        this.expansion = expansion;
        this.originalNpc = originalNpc;
        this.ownerId = owner.getUniqueId();
    }

    @Override
    public void run() {
        this.survivalTicks--;
        if (this.survivalTicks > 0) {
            return;
        }

        ZNPCExpansion expansion = getExpansion();
        NpcConfiguration npcConfiguration = expansion.getNpcConfiguration();
        if(npcConfiguration.isStayUntilEnemyEscape() && this.enemyId != null) {
            if(updateEnemySurvivalTicks()) {
                return;
            }
        }

        CombatNpcManager combatNpcManager = expansion.getCombatNpcManager();
        combatNpcManager.remove(this);
    }

    public void start() {
        setDelay(1L);
        setPeriod(1L);
        resetSurvivalTime();

        TaskScheduler scheduler = getExpansion().getPlugin().getFoliaHelper().getScheduler();
        scheduler.scheduleTask(this);
    }

    public @NotNull Npc getOriginalNpc() {
        return this.originalNpc;
    }

    public @NotNull UUID getOwnerId() {
        return this.ownerId;
    }

    public @NotNull OfflinePlayer getOfflineOwner() {
        return Bukkit.getOfflinePlayer(getOwnerId());
    }

    public void resetSurvivalTime() {
        NpcConfiguration npcConfiguration = getExpansion().getNpcConfiguration();
        long survivalSeconds = npcConfiguration.getSurvivalTime();
        this.survivalTicks = (survivalSeconds * 20L);
    }

    public void setEnemy(@NotNull Player enemy) {
        this.enemyId = enemy.getUniqueId();
    }

    private @NotNull ZNPCExpansion getExpansion() {
        return this.expansion;
    }

    private boolean updateEnemySurvivalTicks() {
        Player enemyPlayer = Bukkit.getPlayer(this.enemyId);
        if (enemyPlayer == null) {
            return false;
        }

        ICombatManager combatManager = getExpansion().getPlugin().getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(enemyPlayer);
        if (tagInformation == null) {
            return false;
        }

        long timeLeftMillis = tagInformation.getMillisLeftCombined();
        this.survivalTicks = ((timeLeftMillis / 50L) + 1L);
        return true;
    }
}

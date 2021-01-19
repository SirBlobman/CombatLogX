package combatlogx.expansion.rewards.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.rewards.RewardExpansion;
import combatlogx.expansion.rewards.manager.RewardManager;

public final class ListenerRewards extends ExpansionListener {
    private final RewardExpansion expansion;
    public ListenerRewards(RewardExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        Player killer = entity.getKiller();
        if(killer == null) return;

        RewardManager rewardManager = this.expansion.getRewardManager();
        rewardManager.checkAll(killer, entity);
    }
}
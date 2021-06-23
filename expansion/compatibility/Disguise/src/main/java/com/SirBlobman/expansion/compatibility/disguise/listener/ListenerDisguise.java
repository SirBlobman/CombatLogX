package com.SirBlobman.expansion.compatibility.disguise.listener;

import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;
import com.SirBlobman.expansion.compatibility.disguise.CompatibilityDisguise;
import com.SirBlobman.expansion.compatibility.disguise.hook.HookLibsDisguises;
import com.SirBlobman.expansion.compatibility.disguise.hook.HookiDisguise;

public class ListenerDisguise implements Listener {
    private final CompatibilityDisguise expansion;
    public ListenerDisguise(CompatibilityDisguise expansion) {
        this.expansion = Objects.requireNonNull(expansion, "expansion must not be null!");
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        boolean undisguised = false;

        if(HookLibsDisguises.isDisguised(player)) {
            HookLibsDisguises.undisguise(player);
            undisguised = true;
        }

        if(HookiDisguise.isDisguised(player)) {
            HookiDisguise.undisguise(player);
            undisguised = true;
        }

        if(undisguised) {
            ILanguageManager languageManager = this.expansion.getPlugin().getCombatLogXLanguageManager();
            String message = languageManager.getMessageColoredWithPrefix("disguise-compatibility-remove-disguise");
            languageManager.sendMessage(player, message);
        }
    }
}

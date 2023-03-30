package combatlogx.expansion.newbie.helper.command;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.ComponentReplacer;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;

public final class SubCommandCheck extends CombatLogCommand {
    private final NewbieHelperExpansion expansion;

    public SubCommandCheck(NewbieHelperExpansion expansion) {
        super(expansion.getPlugin(), "check");
        setPermissionName("combatlogx.command.togglepvp.check");
        this.expansion = expansion;
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Set<String> valueSet = getOnlinePlayerNames();
            return getMatching(args[0], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            return false;
        }

        Player target = findTarget(sender, args[0]);
        if (target == null) {
            return true;
        }

        String targetName = target.getName();
        LanguageManager languageManager = getLanguageManager();
        NewbieHelperExpansion expansion = getExpansion();

        ProtectionManager protectionManager = expansion.getProtectionManager();
        PVPManager pvpManager = expansion.getPVPManager();
        boolean targetProtected = protectionManager.isProtected(target);
        boolean targetPvpStatus = !pvpManager.isDisabled(target);

        String placeholderPath0 = ("placeholder.toggle.");
        String placeholderPath1 = (placeholderPath0 + (targetProtected ? "enabled" : "disabled"));
        String placeholderPath2 = (placeholderPath0 + (targetPvpStatus ? "enabled" : "disabled"));

        Component protectedMessage = languageManager.getMessage(sender, placeholderPath1);
        Component pvpStatusMessage = languageManager.getMessage(sender, placeholderPath2);

        Replacer targetReplacer = new StringReplacer("{target}", targetName);
        Replacer protectedReplacer = new ComponentReplacer("{protected}", protectedMessage);
        Replacer pvpReplacer = new ComponentReplacer("{pvp}", pvpStatusMessage);
        sendMessageWithPrefix(sender, "expansion.newbie-helper.check-format",
                targetReplacer, protectedReplacer, pvpReplacer);
        return true;
    }

    private NewbieHelperExpansion getExpansion() {
        return this.expansion;
    }
}

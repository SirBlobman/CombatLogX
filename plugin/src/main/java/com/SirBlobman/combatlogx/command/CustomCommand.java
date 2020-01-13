package com.SirBlobman.combatlogx.command;

import java.util.Arrays;

import com.SirBlobman.combatlogx.api.shaded.utility.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CustomCommand extends Command implements Listener {
    private final CommandExecutor executor;
    public CustomCommand(String command, CommandExecutor executor, String description, String usage, String... aliases) {
        super(command, description, usage, Util.newList(aliases));
        this.executor = executor;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        boolean success = this.executor.onCommand(sender, this, label, args);
        if(!success) sender.sendMessage(this.usageMessage.replace("<command>", label));
        return success;
    }

    @EventHandler(priority= EventPriority.HIGHEST, ignoreCancelled=true)
    public void beforeCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();
        String[] split = message.split(" ");

        String commandName = split[0];
        if(commandName.startsWith("/")) commandName = commandName.substring(1);
        if(commandName.equals(this.getName()) || this.getAliases().contains(commandName)) {
            e.setCancelled(true);

            String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[0];
            execute(player, commandName, args);
        }
    }
}
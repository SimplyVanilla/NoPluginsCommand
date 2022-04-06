package net.simplyvanilla.nopluginscommand.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SuicideCommandExecutor implements CommandExecutor {

    private final String messageToBroadcast;

    public SuicideCommandExecutor(String messageToBroadcast) {
        this.messageToBroadcast = messageToBroadcast;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is only for players!");
            return true;
        }

        player.setHealth(0);
        if (messageToBroadcast != null && !messageToBroadcast.isEmpty()) {
            Bukkit.broadcastMessage(messageToBroadcast.replaceAll("%name%", player.getName()));
        }
        return true;
    }

}

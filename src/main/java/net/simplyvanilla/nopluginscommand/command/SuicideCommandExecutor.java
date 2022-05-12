package net.simplyvanilla.nopluginscommand.command;

import net.simplyvanilla.nopluginscommand.NoPluginsCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SuicideCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command is only for players!");
            return false;
        }

        player.setHealth(0);

        NoPluginsCommand plugin = NoPluginsCommand.getInstance();
        String messageToBroadcast = plugin.getSuicideBroadcast();

        if (messageToBroadcast != null && !messageToBroadcast.isEmpty()) {
            plugin.getServer().broadcastMessage(messageToBroadcast.replaceAll("%name%", player.getName()));
        }

        return true;
    }

}

package net.simplyvanilla.nopluginscommand.command;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import io.github.miniplaceholders.api.MiniPlaceholders;
import net.simplyvanilla.nopluginscommand.NoPluginsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SuicideCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                miniMessage().deserialize("<red>This command is only for players!</red>"));
            return false;
        }

        player.setHealth(0);

        NoPluginsCommand plugin = NoPluginsCommand.getInstance();
        String messageToBroadcast = plugin.getSuicideBroadcast();

        if (messageToBroadcast != null && !messageToBroadcast.isEmpty()) {
            plugin.getServer().broadcast(miniMessage().deserialize(messageToBroadcast,
                MiniPlaceholders.getAudiencePlaceholders(sender)));
        }

        return true;
    }

}

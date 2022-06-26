package net.simplyvanilla.nopluginscommand;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.Locale;

public class EventsListener implements Listener {

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase().split(" +", 2)[0].replace("/", "");

        NoPluginsCommand plugin = NoPluginsCommand.getInstance();
        Command cmd = resolveCommandAlias(command);
        Player player = event.getPlayer();

        if (cmd == null || (cmd.getPermission() != null && !player.hasPermission(cmd.getPermission()))
            || !plugin.getCommandWhitelist().contains(command)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        if (event.getSender() instanceof Player player) {
            String buffer = event.getBuffer().trim().split(" +", 2)[0].replace("/", "");

            NoPluginsCommand plugin = NoPluginsCommand.getInstance();
            Command cmd = resolveCommandAlias(buffer);

            if (cmd == null || (cmd.getPermission() != null && !player.hasPermission(cmd.getPermission()))
                || !plugin.getCommandWhitelist().contains(buffer.toLowerCase())) {
                event.getCompletions().clear();
            }
        }
    }

    @EventHandler
    public void onServerSendingCommandsToPlayer(PlayerCommandSendEvent event) {
        NoPluginsCommand plugin = NoPluginsCommand.getInstance();
        Player player = event.getPlayer();

        event.getCommands().removeIf(command -> {
            Command cmd = resolveCommandAlias(command);

            return cmd == null || (cmd.getPermission() != null && !player.hasPermission(cmd.getPermission()))
                || !plugin.getCommandWhitelist().contains(command);
        });
    }

    private Command resolveCommandAlias(String input) {
        input = input.toLowerCase(Locale.ROOT);
        if (Bukkit.getCommandAliases().containsKey(input)) {
            return resolveCommandAlias(Bukkit.getCommandAliases().get(input)[0].split(" +", 2)[0]);
        }
        return Bukkit.getCommandMap().getCommand(input);
    }

}

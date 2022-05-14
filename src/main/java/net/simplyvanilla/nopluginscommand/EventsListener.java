package net.simplyvanilla.nopluginscommand;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;

public class EventsListener implements Listener {

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase().split(" +", 2)[0].replace("/", "");

        NoPluginsCommand plugin = NoPluginsCommand.getInstance();
        if (!plugin.getCommandWhitelist().contains(command)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String buffer = event.getBuffer().trim().split(" +", 2)[0].replace("/", "");

        NoPluginsCommand plugin = NoPluginsCommand.getInstance();
        if (!plugin.getCommandWhitelist().contains(buffer.toLowerCase())) {
            event.getCompletions().clear();
        }
    }

    @EventHandler
    public void onServerSendingCommandsToPlayer(PlayerCommandSendEvent event) {
        NoPluginsCommand plugin = NoPluginsCommand.getInstance();

        event.getCommands().removeIf(command -> !plugin.getCommandWhitelist().contains(command));
    }

}

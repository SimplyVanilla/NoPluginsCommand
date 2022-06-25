package net.simplyvanilla.nopluginscommand;

import net.simplyvanilla.nopluginscommand.opdata.OpDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.Collection;

public class EventsListener implements Listener {

    private final OpDataManager opDataManager;

    public EventsListener(OpDataManager opDataManager) {
        this.opDataManager = opDataManager;
    }

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase().split(" +", 2)[0].replace("/", "");
        Player player = event.getPlayer();

        if (!getCommandWhitelist(player).contains(command)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        if (event.getSender() instanceof Player player) {
            String buffer = event.getBuffer().trim().split(" +", 2)[0].replace("/", "");
            if (!getCommandWhitelist(player).contains(buffer.toLowerCase())) {
                event.getCompletions().clear();
            }
        }
    }

    @EventHandler
    public void onServerSendingCommandsToPlayer(PlayerCommandSendEvent event) {
        Collection<String> whitelist = getCommandWhitelist(event.getPlayer());
        event.getCommands().removeIf(command -> !whitelist.contains(command));
    }

    private Collection<String> getCommandWhitelist(Player player) {
        return NoPluginsCommand.getInstance().getCommandWhitelist(opDataManager.getPermissionLevel(player));
    }

}

package net.simplyvanilla.nopluginscommand;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class NoPluginsCommand extends JavaPlugin implements Listener {

    private static final List<String> COMMANDS = Arrays.asList("?", "pl", "about", "version", "ver", "plugins", "bukkit:?", "bukkit:pl", "bukkit:about", "bukkit:version", "bukkit:ver", "bukkit:plugins", "minecraft:pl", "minecraft:plugins", "minecraft:about", "minecraft:version", "minecraft:ver");
    private static final List<String> NO_AUTO_COMPLETE = new ArrayList<>(COMMANDS); // or Arrays.asList("command", "command2");

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        String[] arrCommand = event.getMessage().toLowerCase().split(" ", 2);

        for (String command : COMMANDS) {
            if (arrCommand[0].equalsIgnoreCase("/" + command)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String buffer = event.getBuffer().trim().substring(1);
        event.getSender().sendMessage(buffer);

        if (NO_AUTO_COMPLETE.contains(buffer)) {
            event.getCompletions().clear();
        }
    }

}

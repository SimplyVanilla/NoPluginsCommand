package net.simplyvanilla.nopluginscommand;

import net.simplyvanilla.nopluginscommand.command.CustomTextCommandExecutor;
import net.simplyvanilla.nopluginscommand.command.SuicideCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class NoPluginsCommand extends JavaPlugin implements Listener {

    private Set<String> commandWhitelist;

    @Override
    public void onEnable() {
        File dataFolder = getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        Path configPath = dataFolder.toPath().resolve("config.yml");
        Path customTextPath = dataFolder.toPath().resolve("custom_text.yml");

        if (!Files.exists(configPath)) {
            try {
                Files.copy(getClassLoader().getResourceAsStream("config.yml"), configPath);
            } catch (IOException e) {
                getLogger().severe("Could not copy default config.");
                e.printStackTrace();
            }
        }

        if (!Files.exists(customTextPath)) {
            try {
                Files.copy(getClassLoader().getResourceAsStream("custom_text.yml"), customTextPath);
            } catch (IOException e) {
                getLogger().severe("Could not copy custom text example");
                e.printStackTrace();
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configPath.toFile());
        FileConfiguration customTextConfig = YamlConfiguration.loadConfiguration(customTextPath.toFile());

        String suicideBroadcast = config.getString("suicide-broadcast");

        if (suicideBroadcast != null) {
            suicideBroadcast = ChatColor.translateAlternateColorCodes('&', suicideBroadcast.trim());
        }

        SuicideCommandExecutor suicideCommandExecutor = new SuicideCommandExecutor(suicideBroadcast);
        getCommand("suicide").setExecutor(suicideCommandExecutor);

        this.commandWhitelist = new HashSet<>(config.getStringList("whitelist"));

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("customtext").setExecutor(new CustomTextCommandExecutor(customTextConfig));
    }

    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase().split(" ", 2)[0];

        if (command.length() == 1) { // Command is only /
            return;
        }

        command = command.substring(1);

        if (!commandWhitelist.contains(command)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String buffer = event.getBuffer().trim().split(" +", 2)[0].substring(1);

        if (!commandWhitelist.contains(buffer.toLowerCase())) {
            event.getCompletions().clear();
        }
    }

    @EventHandler
    public void onServerSendingCommandsToPlayer(PlayerCommandSendEvent event) {
        event.getCommands().removeIf(command -> !commandWhitelist.contains(command));
    }

}

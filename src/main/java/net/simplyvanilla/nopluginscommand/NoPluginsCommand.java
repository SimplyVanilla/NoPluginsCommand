package net.simplyvanilla.nopluginscommand;

import net.simplyvanilla.nopluginscommand.command.CustomTextCommandExecutor;
import net.simplyvanilla.nopluginscommand.command.SuicideCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public final class NoPluginsCommand extends JavaPlugin {

    private final Set<String> commandWhitelist = new HashSet<>();
    private String suicideBroadcast;
    private FileConfiguration customTextConfigFile;

    private static NoPluginsCommand instance;

    @Override
    public void onLoad() {
        NoPluginsCommand.instance = this;
    }

    @Override
    public void onEnable() {
        this.reloadConfig();

        getServer().getPluginManager().registerEvents(new EventsListener(), this);

        getCommand("suicide").setExecutor(new SuicideCommandExecutor());
        getCommand("customtext").setExecutor(new CustomTextCommandExecutor());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        this.commandWhitelist.clear();
        this.suicideBroadcast = null;
    }

    @Override
    public void reloadConfig() {
        FileConfiguration configFile = getConfigFile("config.yml");
        this.commandWhitelist.addAll(configFile.getStringList("whitelist"));

        this.suicideBroadcast = getConfigFile("config.yml").getString("suicide-broadcast");
        if (this.suicideBroadcast != null) {
            this.suicideBroadcast = ChatColor.translateAlternateColorCodes('&', this.suicideBroadcast.trim());
        }

        this.customTextConfigFile = getConfigFile("customtext.yml");
        for (String key : customTextConfigFile.getKeys(false)) {
            boolean showInHelp = customTextConfigFile.getBoolean(key + ".show-in-help", false);
            if (!showInHelp) {
                continue;
            }

            boolean needsOp = customTextConfigFile.getBoolean(key + ".help-needs-op", false);
            String description = customTextConfigFile.getString(key + ".help-description", "This command has no description");
            List<String> fullText = customTextConfigFile.getStringList(key + ".help-full-text");

            Bukkit.getHelpMap().addTopic(new SimpleHelpTopic("/" + key, description,
                String.join("\n", fullText), needsOp ? CommandSender::isOp : cs -> true));
        }
    }

    public FileConfiguration getConfigFile(String fileName) {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        Path configPath = dataFolder.toPath().resolve(fileName);
        if (!Files.exists(configPath)) {
            try {
                Files.copy(getClassLoader().getResourceAsStream(fileName), configPath);
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Could not copy '" + fileName + "'", e);
            }
        }

        try {
            return YamlConfiguration.loadConfiguration(configPath.toFile());
        } catch (Exception e) {
            throw new RuntimeException("Could not load '" + fileName + "'", e);
        }
    }

    public Set<String> getCommandWhitelist() {
        return Collections.unmodifiableSet(this.commandWhitelist);
    }

    public String getSuicideBroadcast() {
        return this.suicideBroadcast;
    }

    public FileConfiguration getCustomTextConfigFile() {
        return this.customTextConfigFile;
    }

    public static NoPluginsCommand getInstance() {
        return NoPluginsCommand.instance;
    }
}

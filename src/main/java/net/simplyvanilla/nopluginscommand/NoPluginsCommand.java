package net.simplyvanilla.nopluginscommand;

import com.google.gson.Gson;
import net.simplyvanilla.nopluginscommand.command.CustomTextCommandExecutor;
import net.simplyvanilla.nopluginscommand.command.SuicideCommandExecutor;
import net.simplyvanilla.nopluginscommand.opdata.OpDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

public final class NoPluginsCommand extends JavaPlugin {

    private static final Path OP_DATA_PATH = Paths.get("ops.json");

    private final Map<Integer, Set<String>> commandWhitelists = new HashMap<>();
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

        Gson gson = new Gson();
        OpDataManager opDataManager = new OpDataManager(OP_DATA_PATH, gson);

        getServer().getPluginManager().registerEvents(new EventsListener(opDataManager), this);

        getCommand("suicide").setExecutor(new SuicideCommandExecutor());
        getCommand("customtext").setExecutor(new CustomTextCommandExecutor());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        this.commandWhitelists.clear();
        this.suicideBroadcast = null;
    }

    @Override
    public void reloadConfig() {
        FileConfiguration configFile = getConfigFile("config.yml");

        try {
            MemorySection whitelist = (MemorySection) configFile.get("whitelist");
            whitelist.getKeys(false).forEach(key -> {
                int level = Integer.parseInt(key);

                Set<String> result = new HashSet<>(whitelist.getStringList(key));
                for (int i = 0; i >= 0; i--) {
                    List<String> whitelistedForThatLevel = whitelist.getStringList(String.valueOf(i));

                    if (whitelistedForThatLevel != null) {
                        result.addAll(whitelistedForThatLevel);
                    }
                }

                commandWhitelists.put(level, result);
            });
        } catch (Exception e) {
            getLogger().severe("Could not load whitelist");
            e.printStackTrace();
        }

        this.suicideBroadcast = getConfigFile("config.yml").getString("suicide-broadcast");
        if (this.suicideBroadcast != null) {
            this.suicideBroadcast = ChatColor.translateAlternateColorCodes('&', this.suicideBroadcast.trim());
        }

        this.customTextConfigFile = getConfigFile("customtext.yml");
        for (String key : customTextConfigFile.getKeys(false)) {
            String description = customTextConfigFile.getString(key + ".help-description", "");
            if (description.isEmpty()) {
                continue;
            }

            boolean needsOp = customTextConfigFile.getBoolean(key + ".help-needs-op", false);
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

    public Set<String> getCommandWhitelist(int level) {
        return Collections.unmodifiableSet(commandWhitelists.getOrDefault(level, Collections.EMPTY_SET));
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

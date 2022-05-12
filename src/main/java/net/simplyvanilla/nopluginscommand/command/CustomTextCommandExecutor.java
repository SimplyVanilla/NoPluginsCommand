package net.simplyvanilla.nopluginscommand.command;

import net.simplyvanilla.nopluginscommand.NoPluginsCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomTextCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Please provide an argument!");
            return false;
        }

        NoPluginsCommand plugin = NoPluginsCommand.getInstance();
        FileConfiguration customTextConfig = plugin.getCustomTextConfigFile();

        String messageName = args[0];
        ConfigurationSection section = customTextConfig.getConfigurationSection(messageName);

        if (section == null) {
            sender.sendMessage(ChatColor.RED + "Please provide a valid argument!");
            return false;
        }

        List<String> lines = section.getStringList("lines");

        if (section.contains("page-size")) {
            int itemsPerPage = section.getInt("page-size");

            if (itemsPerPage <= 0) {
                sender.sendMessage(ChatColor.RED + "page-size must greater than 0");
                return false;
            }

            int page = 0;

            if (args.length == 2) {
                try {
                    page = Math.max(0, Integer.parseInt(args[1]) - 1);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not a valid number!");
                    return false;
                }
            } else if (args.length > 2) {
                sender.sendMessage(ChatColor.RED + "Invalid command syntax!");
                return false;
            }

            int startIndex = page * itemsPerPage;
            if (startIndex >= lines.size()) {
                startIndex = Math.max(0, lines.size() - lines.size() % itemsPerPage);
            }

            int endIndex = Math.min(lines.size(), startIndex + itemsPerPage);
            lines = lines.subList(startIndex, endIndex);
        }

        lines.stream()
            .map(line -> ChatColor.translateAlternateColorCodes('&', line))
            .forEach(sender::sendMessage);

        return true;
    }

}

package net.simplyvanilla.nopluginscommand.command;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(miniMessage().deserialize("<red>Please provide an argument!</red>"));
            return false;
        }

        NoPluginsCommand plugin = NoPluginsCommand.getInstance();
        FileConfiguration customTextConfig = plugin.getCustomTextConfigFile();

        String messageName = args[0];
        ConfigurationSection section = customTextConfig.getConfigurationSection(messageName);

        if (section == null) {
            sender.sendMessage(
                miniMessage().deserialize("<red>Please provide a valid argument!</red>"));
            return false;
        }

        List<String> lines = section.getStringList("lines");

        if (section.contains("page-size")) {
            int itemsPerPage = section.getInt("page-size");

            if (itemsPerPage <= 0) {
                sender.sendMessage(
                    miniMessage().deserialize("<red>page-size must greater than 0</red>"));
                return false;
            }

            int page = 0;

            if (args.length == 2) {
                try {
                    page = Math.max(0, Integer.parseInt(args[1]) - 1);
                } catch (NumberFormatException e) {
                    sender.sendMessage(
                        miniMessage().deserialize("<red><input> is not a valid number</red>",
                            Placeholder.unparsed("input", args[1])));
                    return false;
                }
            } else if (args.length > 2) {
                sender.sendMessage(
                    miniMessage().deserialize("<red>Invalid command syntax!</red>"));
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
            .map(miniMessage()::deserialize)
            .forEach(sender::sendMessage);

        return true;
    }

}

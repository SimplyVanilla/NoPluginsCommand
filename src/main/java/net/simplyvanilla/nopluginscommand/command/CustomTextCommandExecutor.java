package net.simplyvanilla.nopluginscommand.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class CustomTextCommandExecutor implements CommandExecutor {

    private final FileConfiguration customTextConfig;
    private final Set<String> keys;

    public CustomTextCommandExecutor(FileConfiguration customTextConfig) {
        this.customTextConfig = customTextConfig;
        this.keys = customTextConfig.getKeys(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Please provide an argument!");
            return true;
        }

        String messageName = args[0];

        List<String> lines = customTextConfig.getStringList(messageName + ".lines");

        if (keys.contains(messageName + ".page-size")) {
            int itemsPerPage = customTextConfig.getInt(messageName + ".page-size");

            if (itemsPerPage <= 0) {
                sender.sendMessage("page-size must greater than 0");
                return true;
            }

            int page = 0;

            if (args.length == 2) {
                try {
                    page = Math.max(0, Integer.parseInt(args[1]) - 1);
                } catch (NumberFormatException e) {
                    sender.sendMessage(args[1] + " is not a valid number!");
                    return true;
                }
            } else if (args.length > 2) {
                sender.sendMessage("Invalid command syntax!");
                return true;
            }

            int startIndex = page * itemsPerPage;
            if (startIndex >= lines.size()) {
                startIndex = Math.max(0, lines.size() - lines.size() % itemsPerPage);
            }

            int endIndex = Math.min(lines.size(), startIndex + itemsPerPage);
            lines = lines.subList(startIndex, endIndex);
        }

        lines.forEach(sender::sendMessage);

        return true;
    }

}

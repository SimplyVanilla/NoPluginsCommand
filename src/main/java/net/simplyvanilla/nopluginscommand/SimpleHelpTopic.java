package net.simplyvanilla.nopluginscommand;

import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SimpleHelpTopic extends HelpTopic {

    private final Predicate<CommandSender> canSeePredicate;

    public SimpleHelpTopic(String name, String description, String fullText) {
        this.name = name;
        this.shortText = description;
        this.fullText = fullText;
        this.canSeePredicate = player -> true;
    }

    public SimpleHelpTopic(String name, String description, String fullText, Predicate<CommandSender> canSeePredicate) {
        this.name = name;
        this.shortText = description;
        this.fullText = fullText;
        this.canSeePredicate = canSeePredicate;
    }

    @Override
    public boolean canSee(@NotNull CommandSender player) {
        return canSeePredicate.test(player);
    }

}

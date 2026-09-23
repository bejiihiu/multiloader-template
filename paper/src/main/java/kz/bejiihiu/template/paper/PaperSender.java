package kz.bejiihiu.template.paper;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import kz.bejiihiu.template.common.command.TemplateSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Адаптер нативного отправителя Paper под общий {@link TemplateSender}.
 *
 * <p>Cloud в modern-менеджере отдаёт {@link CommandSourceStack}, настоящий
 * отправитель лежит в {@code getSender()}. CommandSender в Paper это и
 * Audience (умеет Component), и holder прав — адаптер получается в три строчки.</p>
 */
final class PaperSender implements TemplateSender {

    private final CommandSender sender;

    PaperSender(@NotNull CommandSourceStack stack) {
        this.sender = Objects.requireNonNull(stack, "stack").getSender();
    }

    @Override
    public @NotNull String name() {
        return sender.getName();
    }

    @Override
    public boolean hasPermission(@NotNull String node) {
        return sender.hasPermission(node);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        sender.sendMessage(message);
    }
}

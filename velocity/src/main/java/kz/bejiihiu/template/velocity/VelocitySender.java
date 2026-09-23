package kz.bejiihiu.template.velocity;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import kz.bejiihiu.template.common.command.TemplateSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Адаптер нативного отправителя Velocity под общий {@link TemplateSender}.
 *
 * <p>Тут всё просто: CommandSource из коробки это и Audience
 * (умеет Component напрямую), и holder прав. Адаптер тривиальный,
 * вся логика команд живёт в common.</p>
 */
final class VelocitySender implements TemplateSender {

    private final CommandSource source;

    VelocitySender(@NotNull CommandSource source) {
        this.source = Objects.requireNonNull(source, "source");
    }

    @Override
    public @NotNull String name() {
        if (source instanceof com.velocitypowered.api.proxy.Player player) {
            return player.getUsername();
        }
        return "console";
    }

    @Override
    public boolean hasPermission(@NotNull String node) {
        return source.hasPermission(node);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        source.sendMessage(message);
    }
}

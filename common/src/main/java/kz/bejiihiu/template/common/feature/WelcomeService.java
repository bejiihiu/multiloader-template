package kz.bejiihiu.template.common.feature;

import kz.bejiihiu.template.common.config.PluginConfig;
import kz.bejiihiu.template.common.message.MessageService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Пример фичи: приветствие при входе. Вся логика чистая и платформенно-независимая,
 * поэтому покрывается обычными юнит-тестами без моков сервера.
 *
 * <p>Как добавлять свою фичу: такой же маленький класс на чистых данных,
 * платформа только вызывает его из листенера и отправляет результат.
 * Никакого Bukkit/Velocity API внутри.</p>
 */
public final class WelcomeService {

    private final MessageService messages;

    public WelcomeService(@NotNull MessageService messages) {
        this.messages = Objects.requireNonNull(messages, "messages");
    }

    /**
     * Собирает приветствие для игрока.
     *
     * @return пусто если приветствия выключены в конфиге, иначе готовый компонент
     */
    public @NotNull Optional<Component> welcomeMessage(@NotNull PluginConfig config, @NotNull String playerName) {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(playerName, "playerName");
        if (!config.welcome().enabled()) {
            return Optional.empty();
        }
        // Ник через Placeholder.unparsed: теги внутри ника экранируются,
        // игрок с ником "<red>хакер" не сломает чужое форматирование.
        var body = messages.parse(
                config.welcome().message(),
                Placeholder.unparsed("player", playerName));
        return Optional.of(messages.prefixed(config.prefix(), body));
    }
}

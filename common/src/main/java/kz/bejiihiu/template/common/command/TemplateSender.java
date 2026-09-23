package kz.bejiihiu.template.common.command;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Минимальный порт отправителя команды, общий для paper и velocity.
 *
 * <p>Cloud работает с нативными типами (CommandSourceStack / CommandSource),
 * но бизнес-логике команд они не нужны. Платформенный модуль заворачивает свой
 * тип в эту обёртку одной лямбдой и дальше весь код команд платформенно-независим.</p>
 */
public interface TemplateSender {

    /** Имя отправителя, для логов и плейсхолдеров. */
    @NotNull String name();

    /** Проверка прав. Платформа решает сама: sender.hasPermission на обеих. */
    boolean hasPermission(@NotNull String node);

    /** Отправка уже собранного компонента. Форматированием занимается вызывающий. */
    void sendMessage(@NotNull Component message);
}

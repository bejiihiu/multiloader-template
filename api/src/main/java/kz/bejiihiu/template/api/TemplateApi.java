package kz.bejiihiu.template.api;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Публичное API плагина для сторонних плагинов.
 *
 * <p>Реализация живёт в {@code common} (класс ядра), а платформенные модули
 * регистрируют её при старте и снимают при выключении через {@link Templates}.
 * Зависимостей от Paper/Velocity API тут нет специально: этот модуль можно
 * подключать как compileOnly- spi куда угодно.</p>
 */
public interface TemplateApi {

    /**
     * Версия плагина, та же что в {@code paper-plugin.yml} / {@code velocity-plugin.json}.
     */
    @NotNull String version();

    /**
     * Приветственное сообщение для игрока из актуального конфига.
     * Пусто, если приветствия выключены в конфиге.
     */
    @NotNull Optional<Component> welcomeMessage(@NotNull String playerName);

    /**
     * Перечитывает {@code config.yml} с диска.
     *
     * @return true если конфиг перечитался без ошибок
     */
    boolean reload();
}

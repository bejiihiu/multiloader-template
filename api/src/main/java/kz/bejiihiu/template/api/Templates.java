package kz.bejiihiu.template.api;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Точка входа к {@link TemplateApi} для стороннего кода.
 *
 * <p>Обычный service-locator на волатильном поле: платформенный модуль кладёт
 * реализацию при старте и забирает при выключении. Синхронизации через
 * volatile достаточно, регистрация происходит один раз за жизнь плагина.</p>
 */
public final class Templates {

    private static volatile TemplateApi instance;

    private Templates() {
    }

    /**
     * Регистрирует реализацию API. Повторный вызов заменяет предыдущую —
     * так перезагрузка плагина без рестарта сервера не оставляет висячих ссылок.
     */
    public static void register(@NotNull TemplateApi api) {
        instance = Objects.requireNonNull(api, "api");
    }

    /** Снимает реализацию, вызывать в onDisable / ProxyShutdownEvent. */
    public static void unregister() {
        instance = null;
    }

    /** Текущая реализация, пусто если плагин не запущен. */
    public static @NotNull Optional<TemplateApi> api() {
        return Optional.ofNullable(instance);
    }
}

package kz.bejiihiu.template.common;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Всё, что платформенному модулю нужно отдать ядру при старте.
 *
 * <p>Никаких Paper/Velocity типов: paper положит сюда свой dataFolder и логгер,
 * velocity — свои. Ядро дальше работает только с этим контекстом.</p>
 *
 * @param dataDirectory папка плагина на диске, ядро само создаст её при надобности
 * @param logger        логгер платформы, через него ядро пишет всё кроме чата
 * @param version       версия плагина, на paper берётся из PluginMeta, на velocity из BuildConstants
 */
public record CoreContext(
        @NotNull Path dataDirectory,
        @NotNull org.slf4j.Logger logger,
        @NotNull String version
) {
    public CoreContext {
        Objects.requireNonNull(dataDirectory, "dataDirectory");
        Objects.requireNonNull(logger, "logger");
        Objects.requireNonNull(version, "version");
    }
}

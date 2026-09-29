package kz.bejiihiu.template.common.config;

import org.spongepowered.configurate.transformation.ConfigurationTransformation;

/**
 * Версионные миграции config.yml. Идея подсмотрена у milkdrinkers
 * (VersionedConfig + Migration), реализация на встроенном механизме Configurate.
 *
 * <p>Как это работает: в файле лежит ключ {@code config-version}. При загрузке
 * ConfigManager прогоняет ноду через {@link #migrations()}: все версии новее
 * записанной применяются по порядку, потом ключ обновляется. Файл без ключа
 * считается версией 0 — так конфиги эпохи до версий мигрируются сами.</p>
 *
 * <p>Как добавить миграцию: подними {@link #CURRENT_VERSION} и допиши
 * {@code .addVersion(N, ...)} — либо готовую трансформацию, либо
 * {@code .makeVersion(N, builder -> ...)} для переименований/переносов ключей.
 * Пример: {@code .makeVersion(2, b -> b.addAction(path("old"), (p, n) -> new Object[]{"new"}))}.</p>
 */
public final class ConfigMigrations {

    /** Актуальная версия схемы config.yml. Растёт только вверх. */
    public static final int CURRENT_VERSION = 1;

    private ConfigMigrations() {
    }

    /** Все миграции от версии 0 до {@link #CURRENT_VERSION}. */
    public static ConfigurationTransformation.Versioned migrations() {
        return ConfigurationTransformation.versionedBuilder()
                .versionKey("config-version")
                // v1 — базовая схема, действий нет: просто штампует версию
                // в файлах эпохи до версий. Дальше — только осмысленные миграции.
                .addVersion(1, ConfigurationTransformation.empty())
                .build();
    }
}

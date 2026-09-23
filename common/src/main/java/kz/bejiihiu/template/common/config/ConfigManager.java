package kz.bejiihiu.template.common.config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Загрузка и перезагрузка config.yml.
 *
 * <p>Правила простые и жёсткие:</p>
 * <ul>
 *   <li>файла нет — копируем дефолт из ресурсов jar, ничего не выдумываем;</li>
 *   <li>файл битый — остаёмся на прошлом конфиге, в лог пишем причину, плагин продолжает работать;</li>
 *   <li>в файле не хватает новых ключей — дописываем их значениями по умолчанию.</li>
 * </ul>
 * <p>Плагин никогда не падает из-за конфига. Худшее что случится — warn в логе.</p>
 */
public final class ConfigManager {

    private final Path file;
    private final Logger logger;
    private final YamlConfigurationLoader loader;

    private volatile PluginConfig config = PluginConfig.defaults();

    public ConfigManager(@NotNull Path dataDirectory, @NotNull Logger logger) {
        Objects.requireNonNull(dataDirectory, "dataDirectory");
        this.logger = Objects.requireNonNull(logger, "logger");
        this.file = dataDirectory.resolve("config.yml");
        this.loader = YamlConfigurationLoader.builder().path(file).build();
    }

    /** Актуальный конфиг. Волатильное поле: reload может прийти с другой нити. */
    public @NotNull PluginConfig current() {
        return config;
    }

    /**
     * Перечитывает файл. Потокобезопасно: два параллельных /template reload
     * просто выполнятся друг за другом, конфиг не побьётся.
     *
     * @return true если перечитали без ошибок
     */
    public synchronized boolean reload() {
        try {
            Files.createDirectories(file.getParent());
            if (Files.notExists(file)) {
                copyDefault();
            }
            var node = loader.load();
            var loaded = node.get(PluginConfig.class, PluginConfig.defaults());
            // Самовосстановление: новых ключей в старых файлах нет, дописываем.
            node.set(PluginConfig.class, loaded);
            loader.save(node);
            config = loaded;
            return true;
        } catch (IOException | ConfigurateException e) {
            logger.warn("Не вышло перечитать config.yml ({}), остаёмся на предыдущем конфиге", file, e);
            return false;
        }
    }

    private void copyDefault() throws IOException {
        try (InputStream in = ConfigManager.class.getResourceAsStream("/config.yml")) {
            if (in == null) {
                // Дефолта в ресурсах нет (сборка кривая) — создаём файл из defaults() через лоадер.
                var node = loader.createNode();
                node.set(PluginConfig.class, PluginConfig.defaults());
                loader.save(node);
                return;
            }
            Files.copy(in, file);
        } catch (ConfigurateException e) {
            throw new IOException("Не вышло записать дефолтный конфиг", e);
        }
    }
}

package kz.bejiihiu.template.common.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Objects;

/**
 * Корень config.yml. Record, а не класс: конфиг — это данные, иммутабельность
 * защищает от ситуации «кто-то поменял значение в обход reload».
 *
 * <p>Новые поля добавляются парой: компонент record + ключ в resources/config.yml.
 * ConfigManager при загрузке дописывает недостающие ключи обратно в файл,
 * так что старые конфиги пользователей сами себя чинят.</p>
 */
@ConfigSerializable
public record PluginConfig(
        @Setting("prefix")
        @Comment("Префикс всех сообщений плагина, MiniMessage-формат")
        String prefix,

        @Setting("debug")
        @Comment("Подробные логи: true включает debug-вывод ядра")
        boolean debug,

        @Setting("welcome")
        @Comment("Приветствие при входе игрока")
        Welcome welcome
) {
    public PluginConfig {
        Objects.requireNonNull(prefix, "prefix");
        Objects.requireNonNull(welcome, "welcome");
    }

    /** Секция welcome в config.yml. */
    @ConfigSerializable
    public record Welcome(
            @Setting("enabled")
            @Comment("Слать ли приветствие при входе")
            boolean enabled,

            @Setting("message")
            @Comment("Текст приветствия, <player> подставится автоматически")
            String message
    ) {
        public Welcome {
            Objects.requireNonNull(message, "message");
        }
    }

    /** Дефолты один в один как в resources/config.yml. Два источника правды — зло,
     * поэтому при расхождении побеждает файл, а этот метод лишь страховка на случай битого конфига. */
    public static @NotNull PluginConfig defaults() {
        return new PluginConfig(
                "<gray>[<gradient:#7ed6ff:#4a9eff>Template</gradient>]</gray> ",
                false,
                new Welcome(true, "<green>Привет, <player>! Добро пожаловать на сервер :)")
        );
    }
}

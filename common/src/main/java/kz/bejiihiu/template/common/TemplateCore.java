package kz.bejiihiu.template.common;

import kz.bejiihiu.template.api.TemplateApi;
import kz.bejiihiu.template.api.Templates;
import kz.bejiihiu.template.common.config.ConfigManager;
import kz.bejiihiu.template.common.config.PluginConfig;
import kz.bejiihiu.template.common.feature.WelcomeService;
import kz.bejiihiu.template.common.message.MessageService;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ядро плагина: всё общее состояние и вся бизнес-логика.
 *
 * <p>Создаётся один раз при старте платформы ({@link #bootstrap(CoreContext)}),
 * отдаётся командам и листенерам, гасится при выключении ({@link #shutdown()}).
 * Реализует {@link TemplateApi}, поэтому сторонние плагины видят тот же объект
 * через {@link Templates}.</p>
 *
 * <p>Асинхронность — на виртуальных нитях (Java 21): дешевле платформенных
 * пулов для коротких задач и не требует настройки. Для тяжёлого и долгого
 * лучше платформенный шедулер, он переживает reload.</p>
 */
public final class TemplateCore implements TemplateApi {

    private final CoreContext context;
    private final ConfigManager configs;
    private final MessageService messages = new MessageService();
    private final WelcomeService welcomes;
    private final ExecutorService async;

    private TemplateCore(CoreContext context, ConfigManager configs) {
        this.context = context;
        this.configs = configs;
        this.welcomes = new WelcomeService(messages);
        this.async = Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("template-", 0).factory());
    }

    /** Создаёт ядро: папка плагина, первый конфиг, регистрация в API. */
    public static @NotNull TemplateCore bootstrap(@NotNull CoreContext context) {
        Objects.requireNonNull(context, "context");
        var configs = new ConfigManager(context.dataDirectory(), context.logger());
        configs.reload();
        var core = new TemplateCore(context, configs);
        Templates.register(core);
        context.logger().info("Ядро запущено, версия {}", context.version());
        return core;
    }

    /** Текущий конфиг, всегда не-null: в худшем случае дефолтный. */
    public @NotNull PluginConfig config() {
        return configs.current();
    }

    public @NotNull MessageService messages() {
        return messages;
    }

    public @NotNull WelcomeService welcomes() {
        return welcomes;
    }

    public @NotNull Logger logger() {
        return context.logger();
    }

    /** Исполнитель для фоновых задач. Не для тикающего и не для вечного: такое — в шедулер платформы. */
    public @NotNull ExecutorService async() {
        return async;
    }

    /** Перечитывает конфиг, true если без ошибок. */
    @Override
    public boolean reload() {
        boolean ok = configs.reload();
        if (config().debug()) {
            context.logger().info("Конфиг перечитан: {}", config());
        }
        return ok;
    }

    /** Гасит ядро: пул нитей и снятие с регистрации в API. Вызывать строго при выключении. */
    public void shutdown() {
        Templates.unregister();
        async.close();
        context.logger().info("Ядро остановлено");
    }

    // TemplateApi: сторонний код видит тот же объект, что и платформа.

    @Override
    public @NotNull String version() {
        return context.version();
    }

    @Override
    public @NotNull Optional<Component> welcomeMessage(@NotNull String playerName) {
        return welcomes.welcomeMessage(config(), playerName);
    }
}

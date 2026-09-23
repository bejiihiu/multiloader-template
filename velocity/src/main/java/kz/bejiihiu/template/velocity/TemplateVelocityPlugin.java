package kz.bejiihiu.template.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import kz.bejiihiu.template.common.CoreContext;
import kz.bejiihiu.template.common.TemplateCore;
import kz.bejiihiu.template.common.command.TemplateCommands;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.velocity.VelocityCommandManager;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.time.Duration;

/**
 * Точка входа Velocity-плагина. Создаёт ядро, команды и листенеры,
 * гасит всё при остановке прокси.
 */
@Plugin(
        id = "multiloader-template",
        name = "multiloader-template",
        version = BuildConstants.VERSION,
        description = "я люблю арину btw, это моя бывшая",
        url = "https://devfolia.t.me/",
        authors = {"bejiihiu.xs"}
)
public class TemplateVelocityPlugin {

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;

    private TemplateCore core;

    @Inject
    public TemplateVelocityPlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.core = TemplateCore.bootstrap(new CoreContext(dataDirectory, logger, BuildConstants.VERSION));

        // Менеджер собираем руками: нам не нужен Guice-модуль, хватает identity-маппера
        // поверх нативного CommandSource.
        var container = proxy.getPluginManager().getPlugin("multiloader-template").orElseThrow();
        var commands = new VelocityCommandManager<CommandSource>(
                container,
                proxy,
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity());
        TemplateCommands.register(commands, VelocitySender::new, core);

        // Пример фоновой задачи на шедулере прокси. Всё долгое и периодическое —
        // сюда, а не в пул ядра: шедулер переживает reload и гасится вместе с прокси.
        proxy.getScheduler().buildTask(this,
                        () -> logger.debug("Тик ядра, конфиг: debug={}", core.config().debug()))
                .delay(Duration.ofSeconds(5))
                .repeat(Duration.ofMinutes(1))
                .schedule();
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        if (core != null) {
            core.shutdown();
            core = null;
        }
    }

    @Subscribe
    public void onLogin(PostLoginEvent event) {
        var player = event.getPlayer();
        core.welcomes().welcomeMessage(core.config(), player.getUsername())
                .ifPresent(player::sendMessage);
    }
}

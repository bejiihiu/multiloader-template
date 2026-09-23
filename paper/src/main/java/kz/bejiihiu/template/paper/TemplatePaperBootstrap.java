package kz.bejiihiu.template.paper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import kz.bejiihiu.template.common.CoreContext;
import kz.bejiihiu.template.common.TemplateCore;
import kz.bejiihiu.template.common.command.TemplateCommands;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

/**
 * Бутстраппер Paper-плагина. Выполняется до onEnable, поэтому тут создаются
 * ядро и команды: команды, зарегистрированные на этом этапе, доступны даже
 * в функциях датапаков.
 *
 * <p>Ядро кладётся в {@link PaperServices} — инстансы бутстраппера и плагина
 * разные, другого канала передать объект между ними нет.</p>
 */
public class TemplatePaperBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(final BootstrapContext context) {
        var logger = context.getLogger();
        var version = context.getConfiguration().getVersion();
        var core = TemplateCore.bootstrap(new CoreContext(context.getDataDirectory(), logger, version));
        PaperServices.core(core);

        var commands = PaperCommandManager.builder()
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildBootstrapped(context);
        TemplateCommands.register(commands, PaperSender::new, core);
        PaperServices.commands(commands);

        logger.info("Команды зарегистрированы на этапе bootstrap");
    }
}

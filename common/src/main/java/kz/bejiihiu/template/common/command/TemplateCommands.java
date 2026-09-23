package kz.bejiihiu.template.common.command;

import kz.bejiihiu.template.common.TemplateCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * Все команды плагина в одном месте, один раз, для обеих платформ.
 *
 * <p>Дженерик {@code C} — нативный тип отправителя Cloud-менеджера
 * (CommandSourceStack на paper, CommandSource на velocity). Платформа передаёт
 * адаптер {@code Function<C, TemplateSender>}, дальше всё общее.</p>
 *
 * <p>Права проверяются средствами Cloud ({@code .permission(...)}), дублировать
 * проверки в хендлерах не нужно. Перезагрузка конфига — привилегированная
 * операция, висит на отдельном пермишене.</p>
 */
public final class TemplateCommands {

    private TemplateCommands() {
    }

    public static <C> void register(
            @NotNull CommandManager<C> manager,
            @NotNull Function<C, TemplateSender> senders,
            @NotNull TemplateCore core
    ) {
        Objects.requireNonNull(manager, "manager");
        Objects.requireNonNull(senders, "senders");
        Objects.requireNonNull(core, "core");

        var root = manager.commandBuilder("template");

        // /template ping — проверка что команды вообще доехали до платформы
        manager.command(root.literal("ping")
                .permission("template.command.ping")
                .handler(ctx -> {
                    var sender = senders.apply(ctx.sender());
                    var pong = core.messages().parse(
                            "<green>pong!</green> <gray>Привет, <name> :)</gray>",
                            Placeholder.unparsed("name", sender.name()));
                    sender.sendMessage(core.messages().prefixed(core.config().prefix(), pong));
                }));

        // /template version — версия из paper-plugin.yml / velocity-plugin.json, без хардкода
        manager.command(root.literal("version")
                .permission("template.command.version")
                .handler(ctx -> {
                    var sender = senders.apply(ctx.sender());
                    var text = core.messages().parse(
                            "<gray>Версия: <white><version></white></gray>",
                            Placeholder.unparsed("version", core.version()));
                    sender.sendMessage(core.messages().prefixed(core.config().prefix(), text));
                }));

        // /template reload — перечитывает config.yml, пермишен отдельный и строже
        manager.command(root.literal("reload")
                .permission("template.command.reload")
                .handler(ctx -> {
                    var sender = senders.apply(ctx.sender());
                    boolean ok = core.reload();
                    Component text = ok
                            ? core.messages().parse("<green>Конфиг перезагружен.</green>")
                            : core.messages().parse("<red>Конфиг не перечитался, смотри консоль. Старые значения на месте.</red>");
                    sender.sendMessage(core.messages().prefixed(core.config().prefix(), text));
                }));
    }
}

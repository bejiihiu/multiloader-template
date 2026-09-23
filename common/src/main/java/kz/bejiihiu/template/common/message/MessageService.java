package kz.bejiihiu.template.common.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Тонкая обёртка над MiniMessage, чтобы весь остальной код не таскал за собой
 * синглтоны Adventure и не парсил строки вразнобой.
 *
 * <p>Важно про безопасность: пользовательский ввод (ники, ввод из чата) подставляется
 * только через {@code Placeholder.unparsed/parsed} у вызывающего, никогда конкатенацией
 * в строку. Иначе ник вида {@code <red>} сломает или подменит форматирование —
 * классическая MiniMessage-инъекция.</p>
 */
public final class MessageService {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final PlainTextComponentSerializer plain = PlainTextComponentSerializer.plainText();

    /** Парсит MiniMessage-строку с резолверами (плейсхолдерами). */
    public @NotNull Component parse(@NotNull String text, TagResolver @NotNull ... resolvers) {
        return miniMessage.deserialize(text, resolvers);
    }

    /** Сообщение с префиксом из конфига. Префикс парсится отдельно, чтобы теги
     * из конфига не могли съесть плейсхолдеры самого сообщения. */
    public @NotNull Component prefixed(@NotNull String prefix, @NotNull Component message) {
        return parse(prefix).append(message);
    }

    /** Снимает всё форматирование, для логов и мест где Component не поддерживается. */
    public @NotNull String strip(@NotNull Component component) {
        return plain.serialize(component);
    }
}

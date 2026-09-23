package kz.bejiihiu.template.common.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Швы: публичные методы MessageService. Ожидания — литералы, собранные руками,
 * а не тем же кодом что тестируем, иначе тест тавтологичный.
 */
class MessageServiceTest {

    private final MessageService messages = new MessageService();

    @Test
    void parseHandlesColorsAndDecorations() {
        assertEquals(
                Component.text("pong!", NamedTextColor.GREEN, TextDecoration.BOLD),
                messages.parse("<green><bold>pong!"));
    }

    @Test
    void placeholderSubstitutesValue() {
        assertEquals(
                Component.text("Привет, Steve!"),
                messages.parse("Привет, <name>!", Placeholder.unparsed("name", "Steve")));
    }

    @Test
    void unparsedPlaceholderEscapesUserInput() {
        // Ник с тегами обязан остаться текстом, а не форматированием (MiniMessage-инъекция).
        var result = messages.parse("<name>", Placeholder.unparsed("name", "<red>хакер"));
        assertEquals(Component.text("<red>хакер"), result);
    }

    @Test
    void prefixedPrependsParsedPrefix() {
        var result = messages.prefixed("<gray>[T]</gray> ", Component.text("hi"));
        assertEquals(
                Component.text("[T]", NamedTextColor.GRAY).append(Component.text(" hi")),
                result);
    }

    @Test
    void stripRemovesFormatting() {
        assertEquals(
                "plain text",
                messages.strip(Component.text("plain ").append(
                        Component.text("text", NamedTextColor.RED, TextDecoration.BOLD))));
    }

    @Test
    void parsedMessageContainsNoTagsInPlainForm() {
        var stripped = messages.strip(messages.parse("<rainbow>colorful"));
        assertTrue(stripped.contains("colorful"));
    }
}

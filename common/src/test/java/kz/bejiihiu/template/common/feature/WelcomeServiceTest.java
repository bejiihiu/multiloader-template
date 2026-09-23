package kz.bejiihiu.template.common.feature;

import kz.bejiihiu.template.common.config.PluginConfig;
import kz.bejiihiu.template.common.message.MessageService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Шов: WelcomeService.welcomeMessage. Проверяем поведение (префикс + ник + выключение),
 * а не внутреннее устройство форматирования.
 */
class WelcomeServiceTest {

    private final WelcomeService welcomes = new WelcomeService(new MessageService());

    private static PluginConfig config(boolean enabled) {
        return new PluginConfig("[T] ", false, new PluginConfig.Welcome(enabled, "hi <player>"));
    }

    @Test
    void welcomeContainsPrefixAndPlayerName() {
        var message = welcomes.welcomeMessage(config(true), "Steve");
        assertTrue(message.isPresent());
        assertEquals("[T] hi Steve", new MessageService().strip(message.orElseThrow()));
    }

    @Test
    void disabledWelcomeReturnsEmpty() {
        assertTrue(welcomes.welcomeMessage(config(false), "Steve").isEmpty());
    }

    @Test
    void hostileNicknameStaysLiteral() {
        var message = welcomes.welcomeMessage(config(true), "<red>admin");
        assertTrue(message.isPresent());
        assertEquals("[T] hi <red>admin", new MessageService().strip(message.orElseThrow()));
    }
}

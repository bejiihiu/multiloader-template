package kz.bejiihiu.template.common.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Шов: ConfigManager.reload/current. Гоняем через временную папку:
 * создание дефолта, подхват правок, fail-closed на битом файле.
 */
class ConfigManagerTest {

    @TempDir
    Path temp;

    private ConfigManager manager() {
        return new ConfigManager(temp, LoggerFactory.getLogger("test"));
    }

    @Test
    void missingFileCreatesDefault() {
        var manager = manager();
        assertTrue(manager.reload());
        assertTrue(Files.exists(temp.resolve("config.yml")));
        assertEquals(PluginConfig.defaults().prefix(), manager.current().prefix());
        assertTrue(manager.current().welcome().enabled());
    }

    @Test
    void editedFileIsPickedUp() throws Exception {
        var manager = manager();
        assertTrue(manager.reload());
        Files.writeString(temp.resolve("config.yml"), "prefix: \"[X] \"\ndebug: true\nwelcome:\n  enabled: false\n  message: \"hi\"\n");
        assertTrue(manager.reload());
        assertEquals("[X] ", manager.current().prefix());
        assertTrue(manager.current().debug());
        assertFalse(manager.current().welcome().enabled());
    }

    @Test
    void brokenFileKeepsPreviousConfig() throws Exception {
        var manager = manager();
        assertTrue(manager.reload());
        Files.writeString(temp.resolve("config.yml"), "prefix: [unclosed\n  broken: : :\n");
        assertFalse(manager.reload());
        // Старый конфиг на месте, дефолтный префикс не потерялся.
        assertEquals(PluginConfig.defaults().prefix(), manager.current().prefix());
    }

    @Test
    void missingKeysHealThemselves() throws Exception {
        var manager = manager();
        assertTrue(manager.reload());
        Files.writeString(temp.resolve("config.yml"), "prefix: \"[Y] \"\n");
        assertTrue(manager.reload());
        assertEquals("[Y] ", manager.current().prefix());
        assertEquals(PluginConfig.defaults().welcome(), manager.current().welcome());
        // И файл тоже починился: ключ welcome дописался.
        assertTrue(Files.readString(temp.resolve("config.yml")).contains("welcome:"));
    }
}

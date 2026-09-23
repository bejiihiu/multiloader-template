package kz.bejiihiu.template.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;

/**
 * Лоадер Paper-плагина. Догружает библиотеки в classpath плагина до его старта,
 * поверх уже зашейдженных в jar.
 *
 * <p>Сейчас пустой осознанно: все зависимости зашейджены с релокацией
 * (см. shadowJar в paper/build.gradle.kts). Лоадер понадобится если решишь
 * не шейдить что-то тяжёлое и редкое — тогда тут через
 * {@code builder.addLibrary(new MavenLibraryResolver()...)} можно подтянуть
 * артефакт из Maven-репозитория в рантайме. Пока такого нет, трогать нечего.</p>
 */
public class TemplatePaperLoader implements PluginLoader {

    @Override
    public void classloader(final PluginClasspathBuilder builder) {
        // Все зависимости уже внутри jar, дополнительные не нужны.
    }
}

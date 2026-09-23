// Общие конвенции для всех модулей: Java, кодировка, тесты.
// Платформенные тонкости (тулчейн paper, shade, раннеры) живут в build-файлах самих модулей.
import org.gradle.api.plugins.JavaPluginExtension

subprojects {
    apply(plugin = "java-library")

    group = rootProject.group
    version = rootProject.version

    extensions.configure<JavaPluginExtension> {
        // Базовый тулчейн 21 для common/api/velocity.
        // paper-модуль переопределяет на 25: paper-api 26.x скомпилирован под 25,
        // javac младшей версии такие классы даже прочитать не может.
        toolchain.languageVersion = JavaLanguageVersion.of(21)
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("failed", "skipped")
        }
    }
}

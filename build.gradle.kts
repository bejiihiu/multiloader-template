// Общие конвенции для всех модулей: Java, кодировка, тесты.
// Платформенные тонкости (shade, раннеры) живут в build-файлах самих модулей.
import org.gradle.api.plugins.JavaPluginExtension

subprojects {
    apply(plugin = "java-library")

    group = rootProject.group
    version = rootProject.version

    extensions.configure<JavaPluginExtension> {
        // Единый тулчейн 25 на все модули: paper-api 26.x и velocity-api 4.x
        // скомпилированы под 25, javac младшей версии их классы даже прочитать не может.
        // Недостающий JDK Gradle докачивает сам через Foojay-резолвер из settings.gradle.kts.
        toolchain.languageVersion = JavaLanguageVersion.of(25)
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

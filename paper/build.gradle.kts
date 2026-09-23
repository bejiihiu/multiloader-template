import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.paper.api)
    // Cloud для paper бандлится: платформы его не предоставляют.
    implementation(libs.cloud.paper)
}

java {
    // paper-api 26.x скомпилирован под Java 25: javac младшей версии
    // не прочитает даже его классы, поэтому тут тулчейн 25, а не 21.
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion(libs.versions.minecraft.get())
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        // filteringCharset выставлен, но на expand() он не влияет (проверено:
        // кириллица всё равно бьётся). Правило простое: через expand гоняем
        // только ASCII (версию), весь не-ASCII лежит в yml литералом.
        filteringCharset = "UTF-8"
        val props = mapOf("version" to version)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        archiveBaseName.set("multiloader-template-paper")
        // Склеиваем META-INF/services всех либ (Configurate, Cloud), иначе SPI провайдеры потеряются.
        mergeServiceFiles()
        // Всё своё тащим под своим пакетом, чтобы не конфликтовать с другими плагинами.
        relocate("org.spongepowered.configurate", "kz.bejiihiu.template.libs.configurate")
        relocate("org.yaml.snakeyaml", "kz.bejiihiu.template.libs.snakeyaml")
        relocate("io.leangen.geantyref", "kz.bejiihiu.template.libs.geantyref")
        relocate("org.incendo.cloud", "kz.bejiihiu.template.libs.cloud")
        dependencies {
            // Даёт сервер: Adventure, slf4j и аннотации в jar не кладём.
            exclude(dependency("net.kyori:.*:.*"))
            exclude(dependency("org.slf4j:.*:.*"))
            exclude(dependency("org.jetbrains:annotations:.*"))
        }
    }
}

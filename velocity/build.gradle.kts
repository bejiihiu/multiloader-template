import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.velocity)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)
    // Cloud для velocity бандлится: прокси его не предоставляет.
    implementation(libs.cloud.velocity)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    runVelocity {
        velocityVersion(libs.versions.velocity.api.get())
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        archiveBaseName.set("multiloader-template-velocity")
        // Склеиваем META-INF/services всех либ (Configurate, Cloud), иначе SPI провайдеры потеряются.
        mergeServiceFiles()
        // Всё своё тащим под своим пакетом, чтобы не конфликтовать с другими плагинами.
        relocate("org.spongepowered.configurate", "kz.bejiihiu.template.libs.configurate")
        relocate("org.yaml.snakeyaml", "kz.bejiihiu.template.libs.snakeyaml")
        relocate("io.leangen.geantyref", "kz.bejiihiu.template.libs.geantyref")
        relocate("org.incendo.cloud", "kz.bejiihiu.template.libs.cloud")
        dependencies {
            // Даёт прокси: Adventure, slf4j и аннотации в jar не кладём.
            exclude(dependency("net.kyori:.*:.*"))
            exclude(dependency("org.slf4j:.*:.*"))
            exclude(dependency("org.jetbrains:annotations:.*"))
        }
    }
}

// Версия проксируется в BuildConstants до компиляции, чтобы @Plugin видел константу.
// Механика один в один как в одиночном velocity-шаблоне, путь только под новый пакет.
val templateSource = file("src/main/templates")
val templateDest = layout.buildDirectory.dir("generated/sources/templates")
val generateTemplates = tasks.register<Copy>("generateTemplates") {
    val props = mapOf("version" to project.version)
    inputs.properties(props)

    from(templateSource)
    into(templateDest)
    expand(props)
}

sourceSets.main.configure { java.srcDir(generateTemplates.map { it.outputs }) }

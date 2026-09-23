plugins {
    // Резолвер JDK для тулчейнов через Foojay: без него Gradle 10 откажется
    // докачивать JDK 25 для paper-модуля (deprecation в 9.x).
    id("org.gradle.toolchains.foojay-resolver") version "1.0.0"
}

rootProject.name = "multiloader-template"

include("common", "api", "paper", "velocity")

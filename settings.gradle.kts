plugins {
    // Резолвер JDK для тулчейнов через Foojay: без convention-плагина резолвер
    // не регистрируется и Gradle качает JDK авто-провижинингом (deprecated в 9.x,
    // в 10.x станет ошибкой). Как у milkdrinkers: именно -convention вариант.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "multiloader-template"

include("common", "api", "paper", "velocity")

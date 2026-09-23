# multiloader-template

Paper + Velocity plugin from a single codebase. Shared logic lives in `common`,
platform modules are thin adapters. Built with Gradle (Kotlin DSL), Java 21,
Adventure MiniMessage, Configurate and Incendo Cloud v2.

## Modules

| Module     | Contents |
|------------|----------|
| `api`      | Public SPI for other plugins (`TemplateApi`, `Templates` holder). No platform deps. |
| `common`   | Core: config, messages, commands, example feature, unit tests. No Bukkit/Velocity imports. |
| `paper`    | `JavaPlugin` + `PluginBootstrap` + `PluginLoader`, `paper-plugin.yml`, `runServer`. |
| `velocity` | `@Plugin` entrypoint, `BuildConstants` version injection, `runVelocity`. |

One command tree (`/template ping|version|reload`) is defined once in
`common/.../command/TemplateCommands` and registered on both platforms.

## Requirements

- JDK 21 to build. The paper module compiles with a Java 25 toolchain
  (required by paper-api 26.x) — Gradle downloads it automatically.
- Paper 26.3+ for the paper jar, Velocity 3.5+ for the velocity jar.

## Build & run

```bash
./gradlew build              # tests + both shaded jars
./gradlew :paper:runServer   # Paper dev server on :25565, debugger on :5005
./gradlew :velocity:runVelocity
```

Artifacts: `paper/build/libs/multiloader-template-paper-<version>.jar`,
`velocity/build/libs/multiloader-template-velocity-<version>.jar`.

## Commands & permissions

| Command            | Permission               |
|--------------------|--------------------------|
| `/template ping`   | `template.command.ping`  |
| `/template version`| `template.command.version` |
| `/template reload` | `template.command.reload` |

Commands are registered by Cloud itself — do not list them in `paper-plugin.yml`.

## Configuration

`config.yml` is created from bundled defaults on first start. Missing keys heal
themselves on reload; a broken file keeps the last good config and logs a warning.

## Make it yours

1. `settings.gradle.kts` — `rootProject.name`.
2. `gradle.properties` — `group`, `version`, `description`.
3. Package `kz.bejiihiu.template` → your own (rename dirs + `main`/`bootstrapper`/`loader` in `paper-plugin.yml`, `@Plugin(id)` in velocity).
4. `paper-plugin.yml` / `@Plugin` — `authors`, `website`, `name`.
5. `.github/CODEOWNERS`, `.github/FUNDING.yml` — your username.
6. `LICENSE` — copyright holder.

## Conventions

- Conventional Commits, SemVer (`X.Y.Z` tags → GitHub releases via CI).
- Shared code in `common` is covered by JUnit 5 tests at public seams.
- User input reaches MiniMessage only via `Placeholder.unparsed/parsed` — never by concatenation.
- `api` exposes `TemplateApi`; platform modules register the core on enable and unregister on disable.

## Credits

Structure inspired by [milkdrinkers/Minecraft-Plugin-Template](https://github.com/milkdrinkers/Minecraft-Plugin-Template).
Libraries: [Adventure](https://docs.advntr.dev/), [Configurate](https://github.com/SpongePowered/Configurate),
[Cloud](https://cloud.incendo.org/), [run-task](https://github.com/jpenilla/run-task).

MIT — see `LICENSE`.

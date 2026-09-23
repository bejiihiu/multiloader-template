# Contributing

Коротко: форк → ветка → PR в main. Дальше детали.

## Коммиты

Conventional Commits: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`.
Пример: `feat: add vanish command to common`.

## Код

- Java 21 везде, в paper-модуле тулчейн 25 (требование paper-api 26.x, см. paper/build.gradle.kts).
- Общая логика — в `common`, платформенный код — только адаптеры в `paper`/`velocity`.
- Публичные методы покрываются тестами в `common/src/test` (JUnit 5, швы — публичные интерфейсы).
- Пользовательский ввод в MiniMessage только через `Placeholder.unparsed/parsed`, никогда конкатенацией.
- Комменты в коде — человеческие, по-русски можно. Юзеру видимые строки — грамотный английский.

## Проверки перед PR

```bash
./gradlew build
```

Зелёный build = тесты, shade-сборка обоих jar. Дев-серверы для ручной проверки:
`./gradlew :paper:runServer`, `./gradlew :velocity:runVelocity`.

package kz.bejiihiu.template.paper;

import kz.bejiihiu.template.common.TemplateCore;
import org.incendo.cloud.paper.PaperCommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Мостик между бутстраппером и инстансом плагина.
 *
 * <p>Paper создаёт bootstrap и plugin как два разных объекта без общего состояния,
 * поэтому ядро и менеджер команд передаются через этот холдер. Записывает только
 * бутстраппер, читает только плагин, гонки нет по построению жизненного цикла.</p>
 */
final class PaperServices {

    private static volatile TemplateCore core;
    private static volatile PaperCommandManager<?> commands;

    private PaperServices() {
    }

    static void core(@NotNull TemplateCore value) {
        core = Objects.requireNonNull(value, "core");
    }

    static void commands(@NotNull PaperCommandManager<?> value) {
        commands = Objects.requireNonNull(value, "commands");
    }

    static @NotNull TemplateCore core() {
        return Objects.requireNonNull(core, "Ядро ещё не создано: bootstrap не отработал");
    }
}

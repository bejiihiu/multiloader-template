package kz.bejiihiu.template.paper;

import kz.bejiihiu.template.common.TemplateCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Точка входа Paper-плагина. Тяжёлая работа уже сделана в бутстраппере
 * (ядро + команды), тут только листенеры и задачи шедулера.
 */
public final class TemplatePaperPlugin extends JavaPlugin implements Listener {

    private TemplateCore core;

    @Override
    public void onEnable() {
        this.core = PaperServices.core();

        getServer().getPluginManager().registerEvents(this, this);

        // Пример фоновой задачи на глобальном шедулере: переживает reload,
        // выполняется на сервере, отменяется при выключении.
        getServer().getGlobalRegionScheduler().runAtFixedRate(
                this,
                task -> getSLF4JLogger().debug("Тик ядра, конфиг: debug={}", core.config().debug()),
                20L,
                20L * 60L);
    }

    @Override
    public void onDisable() {
        // Шедулер Paper сам отменяет наши задачи при выключении.
        if (core != null) {
            core.shutdown();
            core = null;
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        var player = event.getPlayer();
        // Player это Audience: Component уходит без легаси-сериализации.
        core.welcomes().welcomeMessage(core.config(), player.getName())
                .ifPresent(player::sendMessage);
    }
}

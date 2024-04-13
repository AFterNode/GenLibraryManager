package cn.afternode.genlibman.spigot;

import cn.afternode.commons.bukkit.configurations.ConfigurationMerger;
import cn.afternode.genlibman.api.GenLibManagerAPI;
import cn.afternode.genlibman.api.GenLibManagerPlatform;
import cn.afternode.genlibman.common.ClassPathAppender;
import cn.afternode.genlibman.common.MavenRepositoryManagerBuilder;
import cn.afternode.genlibman.common.MutableTransferListener;
import cn.afternode.genlibman.common.agent.GenLibManagerAgent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;

public final class GenLibManagerPlugin extends JavaPlugin {
    private final PlatformImpl platform = new PlatformImpl();

    @Override
    public void onLoad() {
        MavenRepositoryManagerBuilder builder = MavenRepositoryManagerBuilder.create();

        // Update config.yml
        File cfgFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigurationMerger.migrate(
                    YamlConfiguration.loadConfiguration(cfgFile),
                    YamlConfiguration.loadConfiguration(new InputStreamReader(
                            Objects.requireNonNull(getResource("config.yml"), "Resource config.yml")
                    ))
            ).save(cfgFile);
            reloadConfig();
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Unable to update config.yml", e);
        }

        // Load repositories from config.yml
        ConfigurationSection repoSec = Objects.requireNonNull(getConfig().getConfigurationSection("repository"), "repository");
        if (repoSec.getBoolean("remove-central", false)) {
            builder.clearRepositories();
            getLogger().info("Central repository removed");
        }
        ConfigurationSection reposSec = Objects.requireNonNull(repoSec.getConfigurationSection("repositories"), "repositories");
        for (String k: reposSec.getKeys(false)) {
            builder.remote(k, repoSec.getString(k));
        }

        // Transfer logged
        MutableTransferListener listener = new MutableTransferListener();
        listener.setStarted((event) -> {
            getLogger().info("Downloading %s".formatted(event.getResource().getResourceName()));
        });
        builder.listener(listener);

        // Classpath appender config
        ConfigurationSection caSec = Objects.requireNonNull(repoSec.getConfigurationSection("classpath"), "classpath");
        if (caSec.getBoolean("use-smart-appender", false)) {
            try {
                GenLibManagerAgent.getInstrumentation();
            } catch (Throwable t) {
                getLogger().log(Level.WARNING, "Self attach failed, smart appender may not works", t);
            }
            builder.appenderFunc(ClassPathAppender::append);
        }
        else builder.appenderFunc(ClassPathAppender::appendB);    // Bukkit platform uses appendB by default

        // Create
        this.platform.repository = builder.build();
        getLogger().info("Repository manager created");

        // Register to service and API
        GenLibManagerAPI.setPlatform(platform);
        Bukkit.getServicesManager().register(
                GenLibManagerPlatform.class,
                platform,
                this,
                ServicePriority.Highest
        );
    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregisterAll(this);
        GenLibManagerAPI.setPlatform(null);
        this.platform.repository.close();
    }
}

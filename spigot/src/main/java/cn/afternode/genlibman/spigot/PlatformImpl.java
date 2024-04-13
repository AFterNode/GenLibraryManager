package cn.afternode.genlibman.spigot;

import cn.afternode.genlibman.api.GenLibManagerPlatform;
import cn.afternode.genlibman.api.MavenRepository;
import cn.afternode.genlibman.api.RemoteRepositoryConfig;
import cn.afternode.genlibman.common.ClassPathAppender;
import cn.afternode.genlibman.common.MavenRepositoryManager;
import cn.afternode.genlibman.common.MavenRepositoryManagerBuilder;

import java.io.File;
import java.io.IOException;

public class PlatformImpl implements GenLibManagerPlatform {
    private final GenLibManagerPlugin plugin;

    MavenRepositoryManager repository;

    PlatformImpl(GenLibManagerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void resolve(String dependency, String testClass, ClassLoader loader) throws IOException {
        this.repository.resolve(dependency, testClass, loader);
    }

    @Override
    public void resolve(String dependency, ClassLoader loader) throws IOException {
        this.repository.resolve(dependency, loader);
    }

    @Override
    public void addRepositories(RemoteRepositoryConfig... config) {
        for (RemoteRepositoryConfig c: config)
            repository.addRepository(c.id(), c.url());
    }

    @Override
    public MavenRepository createStandalone(ClassLoader loader, File targetFolder, boolean useCentral, RemoteRepositoryConfig... remote) throws IOException {
        if (!this.plugin.getConfig().getBoolean("repository.allow-standalone", false)) throw new AssertionError("Standalone repositories creation is disabled in config.yml");
        MavenRepositoryManagerBuilder builder = MavenRepositoryManagerBuilder.create();
        builder.local(targetFolder);
        if (this.plugin.getConfig().getBoolean("classpath.use-smart-appender", false))
            builder.appenderFunc(ClassPathAppender::append);
        else builder.appenderFunc(ClassPathAppender::appendB);
        for (RemoteRepositoryConfig c: remote)
            builder.remote(c.id(), c.url());
        return new MavenRepositoryImpl(loader, builder.build());
    }
}

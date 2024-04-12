package cn.afternode.genlibman.spigot;

import cn.afternode.genlibman.api.GenLibManagerPlatform;
import cn.afternode.genlibman.common.MavenRepositoryManager;

import java.io.IOException;

public class PlatformImpl implements GenLibManagerPlatform {
    MavenRepositoryManager repository;

    PlatformImpl() {}

    @Override
    public void resolve(String dependency, String testClass, ClassLoader loader) throws IOException {
        this.repository.resolve(dependency, testClass, loader);
    }

    @Override
    public void resolve(String dependency, ClassLoader loader) throws IOException {
        this.repository.resolve(dependency, loader);
    }
}

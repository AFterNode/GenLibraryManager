package cn.afternode.genlibman.spigot;

import cn.afternode.genlibman.api.GenLibManagerPlatform;
import cn.afternode.genlibman.common.MavenRepositoryManager;

public class PlatformImpl implements GenLibManagerPlatform {
    MavenRepositoryManager repository;

    PlatformImpl() {}

    @Override
    public MavenRepositoryManager getPlatformRepository() {
        return this.repository;
    }
}

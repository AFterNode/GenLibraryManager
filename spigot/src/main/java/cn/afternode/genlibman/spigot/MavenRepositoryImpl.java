package cn.afternode.genlibman.spigot;

import cn.afternode.genlibman.api.MavenRepository;
import cn.afternode.genlibman.common.MavenRepositoryManager;

import java.io.IOException;

public class MavenRepositoryImpl implements MavenRepository {
    private final ClassLoader ldr;
    private final MavenRepositoryManager mgr;

    MavenRepositoryImpl(ClassLoader ldr, MavenRepositoryManager mgr) {
        this.ldr = ldr;
        this.mgr = mgr;
    }


    @Override
    public void resolve(String dependency, String testClass) throws IOException {
        mgr.resolve(dependency, testClass, ldr);
    }

    @Override
    public void resolve(String dependency) throws IOException {
        mgr.resolve(dependency, ldr);
    }
}

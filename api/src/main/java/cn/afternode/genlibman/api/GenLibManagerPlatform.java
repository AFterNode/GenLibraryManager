package cn.afternode.genlibman.api;

import cn.afternode.genlibman.common.MavenRepositoryManager;
import org.apiguardian.api.API;

import java.io.IOException;

public interface GenLibManagerPlatform {
    void resolve(String dependency, String testClass, ClassLoader loader) throws IOException;
    void resolve(String dependency, ClassLoader loader) throws IOException;
}

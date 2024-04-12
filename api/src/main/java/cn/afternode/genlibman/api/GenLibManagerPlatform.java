package cn.afternode.genlibman.api;

import cn.afternode.genlibman.common.MavenRepositoryManager;
import org.apiguardian.api.API;

import java.io.IOException;

public interface GenLibManagerPlatform {
    /**
     * Check if class exists, or resolve Maven dependency
     * @param dependency Maven dependency
     * @param testClass Test class
     * @param loader Class loader
     */
    @API(status = API.Status.STABLE)
    void resolve(String dependency, String testClass, ClassLoader loader) throws IOException;

    /**
     * Resolve Maven dependency
     * @param dependency Maven dependency
     * @param loader Class loader
     */
    @API(status = API.Status.STABLE)
    void resolve(String dependency, ClassLoader loader) throws IOException;
}

package cn.afternode.genlibman.api;

import org.apiguardian.api.API;

import java.io.File;
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

    /**
     * Add custom Maven repository
     * @param config Repositories
     */
    @API(status = API.Status.EXPERIMENTAL)
    void addRepositories(RemoteRepositoryConfig... config);

    /**
     * Create standalone local repository
     * @param targetFolder Target repository folder
     */
    MavenRepository createStandalone(ClassLoader loader, File targetFolder, boolean useCentral, RemoteRepositoryConfig... remote) throws IOException;
}

package cn.afternode.genlibman.api;

import org.apiguardian.api.API;

import java.io.IOException;

public interface MavenRepository {
    /**
     * Check if class exists, or resolve Maven dependency
     * @param dependency Maven dependency
     * @param testClass Test class
     */
    @API(status = API.Status.STABLE)
    void resolve(String dependency, String testClass) throws IOException;

    /**
     * Resolve Maven dependency
     * @param dependency Maven dependency
     */
    @API(status = API.Status.STABLE)
    void resolve(String dependency) throws IOException;
}

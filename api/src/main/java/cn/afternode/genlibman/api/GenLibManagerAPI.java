package cn.afternode.genlibman.api;

import org.apiguardian.api.API;

public class GenLibManagerAPI {
    private static GenLibManagerPlatform platform;

    /**
     * Get current platform
     */
    @API(status = API.Status.STABLE)
    public static GenLibManagerPlatform getPlatform() {
        return platform;
    }

    /**
     * [INTERNAL] Set current platform
     */
    @API(status = API.Status.INTERNAL)
    public static void setPlatform(GenLibManagerPlatform platform) {
        GenLibManagerAPI.platform = platform;
    }
}

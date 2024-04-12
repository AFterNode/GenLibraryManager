package cn.afternode.genlibman.api;

public class GenLibManagerAPI {
    private static GenLibManagerPlatform platform;

    public static GenLibManagerPlatform getPlatform() {
        return platform;
    }

    public static void setPlatform(GenLibManagerPlatform platform) {
        GenLibManagerAPI.platform = platform;
    }
}

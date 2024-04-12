package cn.afternode.genlibman.common;

import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.transfer.TransferListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MavenRepositoryManagerBuilder {
    private File targetDir;
    private final List<RemoteRepository> repositories = new ArrayList<>();

    private TransferListener transferListener;
    private Consumer<File> appendFunc;

    private MavenRepositoryManagerBuilder() {
        this.targetDir = new File("libraries");
        this.transferListener = new MutableTransferListener();

        this.remote("central", "https://repo.maven.apache.org/maven2");
    }

    public MavenRepositoryManagerBuilder local(File dir) {
        this.targetDir = dir;
        return this;
    }

    public MavenRepositoryManagerBuilder remote(String id, String url) {
        this.repositories.add(
                new RemoteRepository.Builder(id, "default", url)
                        .build()
        );
        return this;
    }

    public MavenRepositoryManagerBuilder clearRepositories() {
        repositories.clear();
        return this;
    }

    public MavenRepositoryManagerBuilder listener(TransferListener listener) {
        this.transferListener = listener;
        return this;
    }

    public MavenRepositoryManagerBuilder appenderFunc(Consumer<File> func) {
        this.appendFunc = func;
        return this;
    }

    public MavenRepositoryManager build() {
        MavenRepositoryManager mgr = new MavenRepositoryManager(targetDir, transferListener, repositories);
        mgr.appenderFunc = appendFunc;
        return mgr;
    }

    public static MavenRepositoryManagerBuilder create() {
        return new MavenRepositoryManagerBuilder();
    }
}

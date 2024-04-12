package cn.afternode.genlibman.common;

import cn.afternode.genlibman.common.agent.GenLibManagerAgent;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.supplier.RepositorySystemSupplier;
import org.eclipse.aether.transfer.TransferListener;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MavenRepositoryManager implements Closeable {
    private boolean closed = false;

    final DefaultRepositorySystemSession session;
    final RepositorySystem repository;
    List<RemoteRepository> repos;

    BiConsumer<File, ClassLoader> appenderFunc = ClassPathAppender::append;

    public MavenRepositoryManager(File dir, TransferListener listener, List<RemoteRepository> repos) {
        this.repository = new RepositorySystemSupplier().get();
        this.session = MavenRepositorySystemUtils.newSession();
        this.session.setSystemProperties(System.getProperties());
        this.session.setChecksumPolicy("fail");
        this.session.setLocalRepositoryManager(this.repository.newLocalRepositoryManager(this.session, new LocalRepository(dir)));
        this.session.setTransferListener(listener);
        this.repos = this.repository.newResolutionRepositories(this.session, repos);
    }

    public void resolve(String name, String checkClass, ClassLoader ldr) throws IOException {
        try {
            Class.forName(checkClass);
        } catch (ClassNotFoundException cnf) {
            resolve(name, ldr);
        }
    }

    public void resolve(String name, ClassLoader ldr) throws IOException {
        assert !closed;

        Artifact artifact = new DefaultArtifact(name);
        Dependency dep = new Dependency(artifact, null);

        DependencyResult result;
        try {
            result = this.repository.resolveDependencies(
                    this.session,
                    new DependencyRequest(new CollectRequest((Dependency) null, Arrays.asList(dep), this.repos), null));
        } catch (DependencyResolutionException e) {
            throw new RuntimeException(e);
        }

//        Instrumentation inst = GenLibManagerAgent.getInstrumentation();
        for (ArtifactResult r: result.getArtifactResults()) {
            appendClasspath(r.getArtifact().getFile(), ldr);
        }
    }

    public void appendClasspath(File file, ClassLoader ldr) {
        assert !closed;
        appenderFunc.accept(file, ldr);
    }

    public void addRepository(String id, String url) {
        assert !closed;
        this.repos.add(new RemoteRepository.Builder(id, "default", url).build());
        this.repos = this.repository.newResolutionRepositories(this.session, this.repos);
    }

    @Override
    public void close() {
        this.closed = true;
    }
}

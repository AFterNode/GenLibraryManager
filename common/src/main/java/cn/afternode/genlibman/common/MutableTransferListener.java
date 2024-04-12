package cn.afternode.genlibman.common;

import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;

import java.util.function.Consumer;

public class MutableTransferListener implements TransferListener {
    private Consumer<TransferEvent> global = null;
    private Consumer<TransferEvent> initiated = null;
    private Consumer<TransferEvent> started = null;
    private Consumer<TransferEvent> progressed = null;
    private Consumer<TransferEvent> corrupted = null;
    private Consumer<TransferEvent> succeeded = null;
    private Consumer<TransferEvent> failed = null;

    @Override
    public void transferInitiated(TransferEvent event) {
        if (global != null) global.accept(event);
        if (initiated != null) initiated.accept(event);
    }

    @Override
    public void transferStarted(TransferEvent event) {
        if (global != null) global.accept(event);
        if (started != null) started.accept(event);
    }

    @Override
    public void transferProgressed(TransferEvent event) {
        if (global != null) global.accept(event);
        if (progressed != null) progressed.accept(event);
    }

    @Override
    public void transferCorrupted(TransferEvent event) {
        if (global != null) global.accept(event);
        if (corrupted != null) corrupted.accept(event);
    }

    @Override
    public void transferSucceeded(TransferEvent event) {
        if (global != null) global.accept(event);
        if (succeeded != null) succeeded.accept(event);
    }

    @Override
    public void transferFailed(TransferEvent event) {
        if (global != null) global.accept(event);
        if (failed != null) failed.accept(event);
    }

    public void setGlobal(Consumer<TransferEvent> global) {
        this.global = global;
    }

    public void setInitiated(Consumer<TransferEvent> initiated) {
        this.initiated = initiated;
    }

    public void setStarted(Consumer<TransferEvent> started) {
        this.started = started;
    }

    public void setCorrupted(Consumer<TransferEvent> corrupted) {
        this.corrupted = corrupted;
    }

    public void setProgressed(Consumer<TransferEvent> progressed) {
        this.progressed = progressed;
    }

    public void setFailed(Consumer<TransferEvent> failed) {
        this.failed = failed;
    }

    public void setSucceeded(Consumer<TransferEvent> succeeded) {
        this.succeeded = succeeded;
    }
}

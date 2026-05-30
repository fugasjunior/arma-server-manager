package cz.forgottenempire.servermanager.support.fakes;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FakeProcess extends Process {

    private final InputStream stdout;
    private final int exitCode;
    private volatile boolean alive;
    private final CompletableFuture<Integer> exitFuture;

    private FakeProcess(byte[] stdoutContent, int exitCode, boolean stayAlive) {
        this.stdout = new ByteArrayInputStream(stdoutContent);
        this.exitCode = exitCode;
        this.alive = stayAlive;
        this.exitFuture = stayAlive ? new CompletableFuture<>() : CompletableFuture.completedFuture(exitCode);
    }

    public static FakeProcess exiting(int code) {
        return new FakeProcess(new byte[0], code, false);
    }

    public static FakeProcess withOutput(String output, int exitCode) {
        return new FakeProcess(output.getBytes(StandardCharsets.UTF_8), exitCode, false);
    }

    public static FakeProcess stayingAlive() {
        return new FakeProcess(new byte[0], 0, true);
    }

    public void terminate() {
        if (alive) {
            alive = false;
            exitFuture.complete(exitCode);
        }
    }

    @Override
    public InputStream getInputStream() {
        return stdout;
    }

    @Override
    public OutputStream getOutputStream() {
        return OutputStream.nullOutputStream();
    }

    @Override
    public InputStream getErrorStream() {
        return InputStream.nullInputStream();
    }

    @Override
    public int waitFor() throws InterruptedException {
        try {
            return exitFuture.get();
        } catch (Exception e) {
            throw new InterruptedException(e.getMessage());
        }
    }

    @Override
    public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            exitFuture.get(timeout, unit);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int exitValue() {
        if (alive) {
            throw new IllegalThreadStateException("Process has not exited");
        }
        return exitCode;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void destroy() {
        terminate();
    }

    @Override
    public Process destroyForcibly() {
        terminate();
        return this;
    }

    @Override
    public long pid() {
        return 99999L;
    }
}

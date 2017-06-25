package javac;

import dao.Privacy;
import utils.Utils;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Executes ClassFile's, uses native java.
 */
public class Executor {
    private final static int DEFAULT_TIMEOUT = 10000; // in milliseconds
    private int timeout;
    private ClassFile classFile;
    private String classPath;
    private String[] args;

    private Process process;

    private String outputMessage;

    private boolean executed = false;

    public Executor(ClassFile classFile) {
        this.classFile = classFile;
        this.timeout = DEFAULT_TIMEOUT;
    }

    /**
     * Writes class file to disk and then executes java on it.
     * @param args arguments as String array for executable class file.
     */
    public void execute(String... args) {
        this.args = args;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(this::runJava);
        executor.shutdown();

        try {
            Utils.write(classFile);
            outputMessage = (String) future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException ignored) {
            outputMessage = "Couldn't execute java process.";
        } catch (TimeoutException e) {
            outputMessage = "Timed out after " + timeout + " milliseconds.";
        } finally {
            future.cancel(true);
            if (process != null) process.destroy();
            Utils.delete(classFile);
        }
        executed = true;
    }

    private String runJava() throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(Utils.createJavaCommand(classFile, classPath, args));
        pb.redirectErrorStream(true);

        process = pb.start();

        String outputMessage = Utils.getLines(process.getInputStream());

        return outputMessage.trim().isEmpty() ? "No output." : outputMessage;
    }

    /**
     * @return String message of execution output.
     * @throws RuntimeException if called before executing.
     */
    public String getOutputMessage() {
        if (!executed) throw new RuntimeException("Must call execute before this method.");
        return outputMessage;
    }

    public void setClassPath(Privacy privacy, Long id) {
        if (privacy == null || id == null)
            throw new NullPointerException("Privacy and id can't be null.");

        if (classPath != null)
            throw new RuntimeException("Classpath is already set.");

        this.classPath = "cache/" + privacy + "/" + id;
    }
}

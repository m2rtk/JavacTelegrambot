package javac;

import dao.Privacy;
import utils.Utils;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Not really a compiler, uses native javac.
 */
public class Compiler {
    private final static int DEFAULT_TIMEOUT = 10000; // in milliseconds
    private int timeout;
    private JavaFile javaFile;
    private String classPath;

    private String outputMessage;
    private ClassFile classFile;

    private boolean compiled = false;

    public Compiler(JavaFile javaFile) {
        this.javaFile = javaFile;
        this.timeout = DEFAULT_TIMEOUT;
    }

    /**
     * Writes javaFile to disk and then executes javac on it.
     * @return boolean of compile success.
     */
    public boolean compile() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(this::runJavac);
        executor.shutdown();

        try {
            Utils.write(javaFile);
            outputMessage = (String) future.get(timeout, TimeUnit.MILLISECONDS);
            classFile = Utils.readClassFile(javaFile);
        } catch (InterruptedException | ExecutionException ignored) {
            outputMessage = "Couldn't execute javac process.";
        } catch (TimeoutException e) {
            outputMessage = "Timed out after " + timeout + " milliseconds.";
        } finally {
            Utils.delete(javaFile);
            Utils.delete(classFile);
        }
        compiled = true;
        return classFile != null;
    }

    private String runJavac() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(Utils.createJavacCommand(javaFile, classPath));
        pb.redirectErrorStream(true);
        Process pro = pb.start();

        pro.waitFor();

        return Utils.getLines(pro.getInputStream());
    }

    /**
     * @return String message. Timed out, compile time error, some other error or success message.
     * @throws RuntimeException if called before compiling.
     */
    public String getOutputMessage() {
        if (!compiled) throw new RuntimeException("Must call compile before this method.");
        return outputMessage;
    }

    /**
     * @return ClassFile created after compiling.
     * @throws RuntimeException if called before compiling.
     */
    public ClassFile getClassFile() {
        if (!compiled) throw new RuntimeException("Must call compile before this method.");
        return classFile;
    }

    public void setClassPath(Privacy privacy, Long id) {
        if (privacy == null || id == null)
            throw new NullPointerException("Privacy and id can't be null.");

        if (classPath != null)
            throw new RuntimeException("Classpath is already set.");

        this.classPath = "cache/" + privacy + "/" + id;
    }
}

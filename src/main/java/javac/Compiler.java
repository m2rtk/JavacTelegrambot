package javac;

import dao.Privacy;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Not really a compiler, uses native javac.
 */
public class Compiler {
    private final static int timeoutms = 1000;
    private JavaFile inputJava;
    private String classPath;

    private String outputMessage;
    private ClassFile outputClass;

    public Compiler(JavaFile inputJava) {
        this.inputJava = inputJava;
    }

    public boolean compile() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(this::runJavac);
        executor.shutdown();

        try {
            Utils.write(inputJava);
            outputMessage = (String) future.get(timeoutms, TimeUnit.MILLISECONDS);
            outputClass   = Utils.readClassFile(inputJava);
        } catch (InterruptedException | ExecutionException ignored) {
            outputMessage = "Couldn't execute javac process.";
        } catch (TimeoutException e) {
            outputMessage = "Timed out after " + timeoutms + " milliseconds.";
        } finally {
            Utils.delete(inputJava);
            Utils.delete(outputClass);
        }
        return outputClass != null;
    }

    private String runJavac() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();

        String[] args;
        if (classPath != null) {
            args = new String[4];
            args[0] = "javac";
            args[1] = "-classpath";
            args[2] = classPath;
            args[3] = inputJava.getClassName() + ".java";
        } else { // mainly for testing
            args = new String[2];
            args[0] = "javac";
            args[1] = inputJava.getClassName() + ".java";
        }
        pb.command(args);
        pb.redirectErrorStream(true);
        Process pro = pb.start();

        pro.waitFor();

        return Utils.getLines(pro.getInputStream());
    }


    public String getOutputMessage() {
        return outputMessage;
    }

    public ClassFile getOutputClass() {
        return outputClass;
    }

    public void setClassPath(Privacy privacy, Long id) { // TODO: 07.06.2017 maybe remove and move to constructor
        if (privacy == null || id == null)
            throw new NullPointerException("Privacy and id can't be null.");

        this.classPath = "cache/" + privacy + "/" + id;
    }
}

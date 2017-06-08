package javac;

import dao.Privacy;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Not really a compiler, uses native javac.
 */
public class Compiler {
    private JavaFile javaFile;
    private String classPath;

    private String outputMessage;
    private ClassFile outputClass;

    public Compiler(JavaFile javaFile) {
        this.javaFile = javaFile;
    }

    public boolean compile() {

        Callable<String> task = () -> {
            try {
                return runJavac();
            } catch (IOException | InterruptedException e) {
                return "";
            }
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(task);
        executor.shutdown();

        try {
            Utils.write(javaFile);
            outputMessage = (String)future.get(10, TimeUnit.SECONDS); // calls runJavac()
            outputClass   = Utils.readClassFile(javaFile);
        } catch (IOException | InterruptedException | ExecutionException ignored) {
            // TODO: 08.06.2017 do something here
        } catch (TimeoutException e) {
            outputMessage = "Timed out after 10 seconds.";
        } finally {
            try {
                Utils.delete(javaFile);
                if (outputClass != null) Utils.delete(outputClass);
            } catch (IOException ignore) {}
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
            args[3] = javaFile.getClassName() + ".java";
        } else { // mainly for testing
            args = new String[2];
            args[0] = "javac";
            args[1] = javaFile.getClassName() + ".java";
        }
        pb.command(args);
        pb.redirectErrorStream(true);
        Process pro = pb.start();

        pro.waitFor(10, TimeUnit.SECONDS);

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

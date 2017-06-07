package javac;

import dao.Privacy;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Executes ClassFile's, uses native java.
 */
public class Executor {
    private ClassFile classFile;
    private String outputMessage;
    private String classPath;

    public Executor(ClassFile classFile) {
        this.classFile = classFile;
    }

    public void run(String... args) {

        Callable<String> task = () -> {
            try {
                return runJava(args);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return "";
            }
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(task);
        executor.shutdown();

        try {
            outputMessage = (String)future.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException ignored) {

        } catch (TimeoutException e) {
            outputMessage = "Timed out after 1 second.";
            future.cancel(true);
        } finally {
            try {
                Utils.delete(classFile.getClassName() + ".class");
            } catch (IOException ignore) {
                // File not found probably
            }
        }
    }

    private String runJava(String... args) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();

        String[] completeArgs;
        if (classPath != null) {
            completeArgs = new String[args.length + 4];
            completeArgs[0] = "java";
            completeArgs[1] = "-classpath";
            completeArgs[2] = classPath;
            completeArgs[3] = classFile.getClassName();
            System.arraycopy(args, 0, completeArgs, 4, args.length);
        } else { // mainly for testing
            completeArgs = new String[args.length + 2];
            completeArgs[0] = "java";
            completeArgs[1] = classFile.getClassName();
            System.arraycopy(args, 0, completeArgs, 2, args.length);
        }

        pb.command(completeArgs);
        pb.redirectErrorStream(true);
        String outputMessage = null;

        try {
            Process pro = pb.start();
            pro.waitFor(1, TimeUnit.SECONDS);
            outputMessage = Utils.getLines(pro.getInputStream());
            if (outputMessage.trim().isEmpty()) outputMessage = "No output.";
        } catch (InterruptedException ignored) {

        }
        return outputMessage;
    }

    public String getOutputMessage() {
        return outputMessage;
    }

    public void setClassPath(Privacy privacy, Long id) { // TODO: 07.06.2017 maybe remove and move to constructor
        if (privacy == null || id == null)
            throw new NullPointerException("Privacy and id can't be null.");

        this.classPath = "cache/" + privacy + "/" + id;
    }
}

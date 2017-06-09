package javac;

import dao.Privacy;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Executes ClassFile's, uses native java.
 */
public class Executor {
    private final static int timeoutms = 1000;
    private ClassFile inputClass;
    private String classPath;
    private String[] args;

    private String outputMessage;

    public Executor(ClassFile inputClass) {
        this.inputClass = inputClass;
    }

    public void run(String... args) {
        this.args = args;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(this::runJava);
        executor.shutdown();

        try {
            Utils.write(inputClass);
            outputMessage = (String) future.get(timeoutms, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException ignored) {
            outputMessage = "Couldn't execute java process.";
        } catch (TimeoutException e) {
            outputMessage = "Timed out after " + timeoutms + " milliseconds.";
        } finally {
            future.cancel(true);
            Utils.delete(inputClass);
        }
    }

    private String runJava() throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder();

        String[] completeArgs;
        if (classPath != null) {
            completeArgs = new String[args.length + 4];
            completeArgs[0] = "java";
            completeArgs[1] = "-classpath";
            completeArgs[2] = classPath;
            completeArgs[3] = inputClass.getClassName();
            System.arraycopy(args, 0, completeArgs, 4, args.length);
        } else { // mainly for testing
            completeArgs = new String[args.length + 2];
            completeArgs[0] = "java";
            completeArgs[1] = inputClass.getClassName();
            System.arraycopy(args, 0, completeArgs, 2, args.length);
        }

        pb.command(completeArgs);
        pb.redirectErrorStream(true);

        Process pro = pb.start();
        pro.waitFor();

        String outputMessage = Utils.getLines(pro.getInputStream());

        return outputMessage.trim().isEmpty() ? "No output." : outputMessage;
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

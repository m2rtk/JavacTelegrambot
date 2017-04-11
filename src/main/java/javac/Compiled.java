package javac;

import org.telegram.telegrambots.logging.BotLogger;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.*;

public class Compiled {
    private String name;
    private String out;

    private byte[] byteCode;


    public Compiled(byte[] byteCode, String name) {
        this.byteCode = byteCode;
        this.name = name;
    }

    public void run(String... args) {
        final Callable task = (Callable<String>) () -> {
            try {
                return runJava(args);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return "";
            }
        };

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future future = executor.submit(task);
        executor.shutdown();

        try {
            out  = (String)future.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException ignored) {

        } catch (TimeoutException e) {
            out = "Timed out after 1 seconds";
            future.cancel(true);
        } finally {
            try {
                Utils.delete(name + ".class");
            } catch (IOException ignore) {
                // File not found probably
            }
        }
    }

    private String runJava(String... args) throws IOException, InterruptedException {

        Utils.writeSmallBinaryFile(byteCode, name + ".class");

        ProcessBuilder pb = new ProcessBuilder();
        String[] completeArgs = new String[args.length + 2];
        completeArgs[0] = "java";
        completeArgs[1] = name;
        System.arraycopy(args, 0, completeArgs, 2, args.length);
        pb.command(completeArgs);
        pb.redirectErrorStream(true);


        Process pro = pb.start();
        String out;
        try {
            pro.waitFor(1, TimeUnit.SECONDS);
            out = Utils.getLines(pro.getInputStream());
        } catch (InterruptedException e) {
            System.out.println("INTERRUPTEDD");
            out = "";
        }
        return out;
    }

    public String getOut() {
        return out;
    }

    public String getName() {
        return name;
    }

    public byte[] getByteCode() {
        return byteCode;
    }
}

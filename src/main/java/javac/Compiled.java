package javac;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.*;

import static dao.BotDAO.Privacy;

public class Compiled {
    private String name;
    private String out;

    private byte[] byteCode;

    private Privacy privacy;
    private Long id;
    private String classPath;

    public Compiled(byte[] byteCode, String name, Privacy privacy, Long id) {
        this.byteCode = byteCode;
        this.name = name;
        this.privacy = privacy;
        this.id = id;

        if (id != null && privacy != null)
            this.classPath = "cache/" + privacy + "/" + id;
    }

    public void run(String... args) {
        final Callable<String> task = () -> {
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
            out = "Timed out after 1 second.";
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
        ProcessBuilder pb = new ProcessBuilder();

        String[] completeArgs;
        if (classPath != null) {
            completeArgs = new String[args.length + 4];
            completeArgs[0] = "java";
            completeArgs[1] = "-classpath";
            completeArgs[2] = classPath;
            completeArgs[3] = name;
            System.arraycopy(args, 0, completeArgs, 4, args.length);
        } else { // mainly for testing
            completeArgs = new String[args.length + 2];
            completeArgs[0] = "java";
            completeArgs[1] = name;
            System.arraycopy(args, 0, completeArgs, 2, args.length);
        }

        pb.command(completeArgs);
        pb.redirectErrorStream(true);
        String out = null;

        try {
            Process pro = pb.start();
            pro.waitFor(1, TimeUnit.SECONDS);
            out = Utils.getLines(pro.getInputStream());
            if (out.trim().isEmpty()) out = "No output.";
        } catch (InterruptedException ignored) {
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

    public Privacy getPrivacy() {
        return privacy;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Compiled{" +
                "name='" + name + '\'' +
                ", out='" + out + '\'' +
                ", byteCode=" + Arrays.toString(byteCode) +
                ", privacy=" + privacy +
                ", id=" + id +
                '}';
    }
}

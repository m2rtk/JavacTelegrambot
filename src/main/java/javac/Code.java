package javac;

import dao.BotDAO;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.*;

import static dao.BotDAO.Privacy;

public class Code {
    private String source;
    private Privacy privacy;
    private Long id;
    private String name;
    private String out;

    private Compiled compiled;

    public Code(String source, Privacy privacy, Long id) {
        this.source = source;
        this.privacy = privacy;
        this.id = id;
    }

    private String getClassName() {
        int i = source.indexOf("public class ");
        if (i == -1) return "";
        return source.substring(i + 13).split("\\{")[0].trim();
    }

    public boolean compile() {
        this.name = getClassName();
        boolean result = false;


        final Callable task = (Callable<String>) () -> {
            try {
                return runJavac();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return "";
            }
        };

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future future = executor.submit(task);
        executor.shutdown();

        try {
            Utils.writeFile(source, name + ".java");
            out  = (String)future.get(10, TimeUnit.SECONDS);
            result = Utils.exists(name + ".class");
            this.compiled = new Compiled(Utils.readSmallBinaryFile(name + ".class"), name, privacy, id);
        } catch (IOException | InterruptedException | ExecutionException ignored) {

        } catch (TimeoutException e) {
            out = "Timed out after 10 seconds.";
        } finally {
            try {
                Utils.delete(name + ".class");
                Utils.delete(name + ".java");
            } catch (IOException ignore) {
                // File not found probably
            }
        }
        return result;
    }

    private String runJavac() throws IOException, InterruptedException {

        Utils.writeFile(source, name + ".java");

        ProcessBuilder pb = new ProcessBuilder();
        String[] args = new String[4];
        args[0] = "javac";
        args[1] = "-classpath";
        args[2] = "cache/" + privacy + "/" + id;
        args[3] = name + ".java";
        pb.command(args);
        pb.redirectErrorStream(true);
        Process pro = pb.start();
        String out = null;
        try {
            pro.waitFor(1, TimeUnit.MILLISECONDS);
            out = Utils.getLines(pro.getInputStream());
        } catch (InterruptedException ignored) {
            // out is handled in compile method.
        }
        return out;
    }


    public String getOut() {
        return out;
    }

    public Compiled getCompiled() {
        return compiled;
    }

    public String getName() {
        return name;
    }
}

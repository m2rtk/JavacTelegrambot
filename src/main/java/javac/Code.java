package javac;

import dao.Privacy;

import java.io.IOException;
import java.util.concurrent.*;


public class Code {
    private String source;
    private String name;
    private String out;

    private Compiled compiled;

    private Privacy privacy;
    private Long id;
    private String classPath;

    public Code(String source, Privacy privacy, Long id) {
        this.source = source;
        this.privacy = privacy;
        this.id = id;

        if (id != null && privacy != null)
            this.classPath = "cache/" + privacy + "/" + id;
    }

    private String getClassName() {
        int i = source.indexOf("public class ");
        if (i == -1) return "";
        return source.substring(i + 13).split("\\{")[0].trim();
    }

    public boolean compile() {
        this.name = getClassName();
        boolean result = false;

        final Callable<String> task = () -> {
            try {
                return runJavac();
            } catch (IOException | InterruptedException e) {
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
            compiled = new Compiled(Utils.readSmallBinaryFile(name + ".class"), name);
            if (privacy != null && id != null) compiled.setPrivacyAndId(privacy, id);
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
        ProcessBuilder pb = new ProcessBuilder();

        String[] args;
        if (classPath != null) {
            args = new String[4];
            args[0] = "javac";
            args[1] = "-classpath";
            args[2] = classPath;
            args[3] = name + ".java";
        } else { // mainly for testing
            args = new String[2];
            args[0] = "javac";
            args[1] = name + ".java";
        }
        pb.command(args);
        pb.redirectErrorStream(true);
        Process pro = pb.start();

        pro.waitFor(10, TimeUnit.SECONDS);

        return Utils.getLines(pro.getInputStream());
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

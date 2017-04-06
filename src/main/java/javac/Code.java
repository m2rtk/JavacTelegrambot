package javac;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Code {
    private String source;
    private String name;
    private String out;

    private Compiled compiled;

    public Code(String source) {
        this.source = source;
    }

    private String getClassName() {
        int i = source.indexOf("public class ");
        if (i == -1) return "";
        return source.substring(i + 13).split("\\{")[0].trim();
    }

    public boolean compile() {
        this.name = getClassName();
        boolean result = false;

        try {
            Utils.writeFile(source, name + ".java");
            out = runJavac();
            result = Utils.exists(name + ".class");
            this.compiled = new Compiled(Utils.readSmallBinaryFile(name + ".class"), name);
        } catch (IOException | InterruptedException ignored) {

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
        pb.command("javac", name + ".java");
        pb.redirectErrorStream(true);
        Process pro = pb.start();
        String out;
        try {
            pro.waitFor(1, TimeUnit.SECONDS);
            out = Utils.getLines(">", pro.getInputStream());
        } catch (InterruptedException e) {
            System.out.println("INTERRUPTED");
            out = "";
        }
        System.out.println("javac exitValue() " + pro.exitValue());
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
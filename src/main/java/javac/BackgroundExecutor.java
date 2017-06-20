package javac;

import dao.Privacy;

import java.io.IOException;

public class BackgroundExecutor {

    private final ClassFile classFile;
    private String[] args;
    private String classPath;
    private Process process;

    public BackgroundExecutor(ClassFile classFile) {
        this.classFile = classFile;
    }

    public void run(String... args) {
        this.args = args;
    }

    public void runJava() throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process pro = rt.exec(Utils.createJavaCommand(classFile, classPath, args));
    }

    public void setClassPath(Privacy privacy, Long id) { // TODO: 07.06.2017 maybe remove and move to constructor
        if (privacy == null || id == null)
            throw new NullPointerException("Privacy and id can't be null.");

        if (classPath != null)
            throw new RuntimeException("Classpath is already set.");

        this.classPath = "cache/" + privacy + "/" + id;
    }
}
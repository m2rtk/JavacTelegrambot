package javac;

import bot.UpdateHandler;
import dao.BotDAO;
import dao.Privacy;

import java.io.IOException;

public class BackgroundExecutor {

    private final ClassFile classFile;
    private String[] args;
    private String classPath;
    private UpdateHandler botThread;
    private BackgroundJavaProcess thread;

    public BackgroundExecutor(ClassFile classFile, UpdateHandler botThread) {
        this.classFile = classFile;
        this.botThread = botThread;
    }

    public int run(BotDAO dao, String... args)  {
        this.args = args;
        try {
            return runJava(dao);
        } catch (IOException e) {
            System.out.println("ssh, is ok");
            return -1;
        }
    }

    private int runJava(BotDAO dao) throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(Utils.createJavaCommand(classFile, classPath, args));
        pb.redirectErrorStream(true);

        thread = new BackgroundJavaProcess(pb, botThread, dao);
        thread.start();
        return thread.getPid();
    }

    public void setClassPath(Privacy privacy, Long id) { // TODO: 07.06.2017 maybe remove and move to constructor
        if (privacy == null || id == null)
            throw new NullPointerException("Privacy and id can't be null.");

        if (classPath != null)
            throw new RuntimeException("Classpath is already set.");

        this.classPath = "cache/" + privacy + "/" + id;
    }
}
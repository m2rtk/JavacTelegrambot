package javac;

import bot.UpdateHandler;
import dao.BotDAO;
import dao.Privacy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BackgroundJavaProcess extends Thread {
    private static final Logger logger = LogManager.getLogger(BackgroundJavaProcess.class);

    private static int pid_counter = 0;
    private final  int pid;
    private final UpdateHandler botThread;
    private final String[] args;
    private final BotDAO dao;
    private final ClassFile classFile;
    private final ProcessBuilder pb;

    private String classPath;
    private Process process;

    private boolean isDead = false;

    public BackgroundJavaProcess(UpdateHandler botThread, BotDAO dao, ClassFile classFile, String... args) {
        this.pb = new ProcessBuilder();
        this.classFile = classFile;
        this.args = args;
        this.botThread = botThread;
        this.dao = dao;
        this.pid = pid_counter++;
        this.dao.addJavaProcess(this, this.botThread.getUpdate().getMessage().getChatId());

        this.setName(classFile.getClassName() + "-" + pid + "-" + botThread.getName());
    }

    public int getPid() {
        return pid;
    }

    @Override
    public void run() {
        pb.redirectErrorStream(true);
        pb.command(Utils.createJavaCommand(classFile, classPath, args));
        try {
            process = pb.start();
            logger.info("Started process " + pid);

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.ISO_8859_1));

                String line;
                while ((line = in.readLine()) != null) botThread.sendMessage(Utils.toMonospace(line));

            } catch (IOException e) {
                logger.error(e);
                for (StackTraceElement ste : e.getStackTrace()) logger.error("\t\t" + ste);
            }

        } catch (IOException e) {
            logger.error(e);
            for (StackTraceElement ste : e.getStackTrace()) logger.error("\t\t" + ste);
        }
        kill();
    }

    public void kill() {
        if (isDead) return;
        isDead = true;

        dao.removeJavaProcess(pid, botThread.getUpdate().getMessage().getChatId());
        if (process.isAlive()) process.destroy();

        botThread.sendMessage(Utils.toMonospace("Process " + pid + " terminated."));
    }

    public void setClassPath(Privacy privacy, Long id) {
        if (privacy == null || id == null)
            throw new NullPointerException("Privacy and id can't be null.");

        if (classPath != null)
            throw new RuntimeException("Classpath is already set.");

        this.classPath = "cache/" + privacy + "/" + id;
    }

    public Process getProcess() {
        return process;
    }
}

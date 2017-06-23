package javac;

import bot.UpdateHandler;
import dao.BotDAO;
import dao.Privacy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BackgroundJavaProcess extends Thread {
    private static int pid_counter = 0;
    private final  int pid;
    private final UpdateHandler bot;
    private final ProcessBuilder pb;
    private final BotDAO dao;

    private String classPath;
    private Process process;

    private boolean isDead = false;

    public BackgroundJavaProcess(UpdateHandler botThread, BotDAO dao, ClassFile classFile, String... args) {
        this.pb = new ProcessBuilder();
        this.pb.redirectErrorStream(true);
        this.pb.command(Utils.createJavaCommand(classFile, classPath, args));

        this.bot = botThread;
        this.dao = dao;
        this.dao.addJavaProcess(this, bot.getChat());
        this.pid = pid_counter++;
    }

    public int getPid() {
        return pid;
    }

    @Override
    public void run() {
        try {
            process = pb.start();

            try {
                String line;
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.ISO_8859_1));

                while ((line = in.readLine()) != null) {
                    bot.sendMessage(line);
                }

            } catch (IOException e) {
                System.out.println("watwat");
            }

        } catch (IOException e) {
            System.out.println("wutwut");
        }
        kill();
    }

    public void kill() {
        if (isDead) return;

        dao.removeJavaProcess(pid, bot.getChat());
        if (process.isAlive()) process.destroy();
        bot.sendMessage("Yo I'm out. Signed " + pid);

        isDead = true;
    }

    public void setClassPath(Privacy privacy, Long id) { // TODO: 07.06.2017 maybe remove and move to constructor
        if (privacy == null || id == null)
            throw new NullPointerException("Privacy and id can't be null.");

        if (classPath != null)
            throw new RuntimeException("Classpath is already set.");

        this.classPath = "cache/" + privacy + "/" + id;
    }
}

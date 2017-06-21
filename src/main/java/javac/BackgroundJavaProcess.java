package javac;

import bot.UpdateHandler;
import dao.BotDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BackgroundJavaProcess extends Thread {
    private static int pid_counter = 0;
    private final int pid;
    private final UpdateHandler bot;
    private final ProcessBuilder pb;
    private final BotDAO dao;
    private Process process;

    public BackgroundJavaProcess(ProcessBuilder pb, UpdateHandler botThread, BotDAO dao) {
        this.pb = pb;
        this.bot = botThread;
        this.pid = pid_counter++;
        this.dao = dao;
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
        dao.removeJavaProcess(pid, bot.getChat());
        if (process.isAlive()) process.destroy();
        bot.sendMessage("Yo I'm out. Signed " + pid);
    }
}

package bot;

import java.util.Scanner;

/**
 * Created on 19.05.2017.
 */
public class DirectInputThread implements Runnable {

    private final JavaBot bot;

    public DirectInputThread(JavaBot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String reply = scanner.nextLine();
                String[] pieces = reply.split(" ");
                if (pieces.length > 2 && pieces[0].equals("send")) {
                    try {
                        Long chatId = Long.parseLong(pieces[1]);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 2; i < pieces.length; i++) sb.append(pieces[i]).append(" ");
                        bot.sendMessage(sb.toString(), chatId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

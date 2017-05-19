package bot.commands;

import bot.Commands;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class HelpCommand implements Command {
    private static final String TAG = "HelpCommand";
    private static String output;

    public HelpCommand() {
        if (output == null) {
            try {
                init();
            } catch (Exception e) {
                BotLogger.severe(TAG, e);
            }
        }
    }

    @Override
    public void execute() {
        // nothing to do here
    }

    @Override
    public String getOutput() {
        return output;
    }

    @Override
    public String getName() {
        return Commands.help;
    }

    /**
     * Reads output text from file and loads it into memory.
     * This method should only be called once in the systems life.
     * IllegalStateException is thrown if called a second time.
     * @throws IOException if file not found.
     * @throws URISyntaxException if filepath is bad.
     */
    private static void init() throws IOException, URISyntaxException {
        System.out.println("Init()");
        if (output != null) throw new IllegalStateException();
        StringBuilder sb = new StringBuilder();
        List<String> lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("HelpMessage.txt").toURI()));
        lines.forEach(s -> sb.append(s).append("\n"));
        output = sb.toString();
    }
}

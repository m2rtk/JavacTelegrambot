package bot.commands;

import bot.Commands;
import com.google.common.io.Resources;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand implements Command {
    private static final String TAG = "HelpCommand";
    private static String output;

    static {
        try {
            output = String.join("\n", Files.readAllLines(
                    Paths.get(ClassLoader.getSystemResource("HelpMessage.txt").toURI()))
            );
        } catch (Exception e) {
            output = "Couldn't load help.";
            BotLogger.severe(TAG, e);
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
}

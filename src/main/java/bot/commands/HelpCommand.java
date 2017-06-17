package bot.commands;

import org.telegram.telegrambots.logging.BotLogger;

import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * This is the only Command implementation that does not use setOutput(String)
 * This is because the static block does not allow calling an object method from inside it.
 * I really like the static block.
 */

public class HelpCommand extends Command {
    private static final String TAG = "HelpCommand";
    private static String output;

    // this makes sure that the help text is read into memory only once in the systems runtime
    static {
        try {
            output = String.join(System.getProperty("line.separator"), Files.readAllLines(
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
    public String toString() {
        return "HelpCommand{}";
    }
}

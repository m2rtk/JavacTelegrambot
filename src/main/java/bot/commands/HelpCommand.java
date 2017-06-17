package bot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.logging.BotLogger;

import java.nio.file.Files;
import java.nio.file.Paths;

public class HelpCommand extends Command {
    private static final Logger logger = LogManager.getLogger(HelpCommand.class);
    private static String output;

    // this makes sure that the help text is read into memory only once in the systems runtime
    static {
        try {
            output = String.join(System.getProperty("line.separator"), Files.readAllLines(
                    Paths.get(ClassLoader.getSystemResource("HelpMessage.txt").toURI()))
            );
        } catch (Exception e) {
            output = "Couldn't load help.";
            logger.error(e);
        }
    }

    @Override
    public void execute() {
        setOutput(output);
    }

    @Override
    public String toString() {
        return "HelpCommand{}";
    }
}

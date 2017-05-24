package bot.commands;

import bot.Commands;
import bot.commands.interfaces.Command;
import bot.commands.interfaces.Parameter;
import org.telegram.telegrambots.logging.BotLogger;

import java.nio.file.Files;
import java.nio.file.Paths;

public class HelpCommand implements Command {
    private static final String TAG = "HelpCommand";
    private static String output;

    // this makes sure that the help text is read into memory only once in the systems runtime
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
    public void acceptParameter(Parameter parameterVisitor) {
        parameterVisitor.visit(this);
    }

    @Override
    public String getOutput() {
        return output;
    }

    @Override
    public String getName() {
        return Commands.help;
    }

    @Override
    public String toString() {
        return "HelpCommand{}";
    }
}

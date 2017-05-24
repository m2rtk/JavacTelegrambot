package bot.commands;

import bot.Commands;
import bot.commands.interfaces.Command;
import bot.commands.interfaces.Parameter;

public class NiceCommand extends Command {

    @Override
    public void execute() {
        // nothing to do here
    }

    @Override
    public String getOutput() {
        return "nice";
    }

    @Override
    public String getName() {
        return Commands.nice;
    }

    @Override
    public String toString() {
        return "NiceCommand{}";
    }
}

package bot.commands;

import bot.Commands;
import bot.commands.visitors.Command;

public class NiceCommand extends Command {

    @Override
    public void execute() {
        setOutput("nice");
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

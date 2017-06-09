package bot.commands;

import bot.Commands;

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

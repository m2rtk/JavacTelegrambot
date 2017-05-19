package bot.commands;

import bot.Commands;

public class NiceCommand implements Command {

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
}

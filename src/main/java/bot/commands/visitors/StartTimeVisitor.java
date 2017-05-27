package bot.commands.visitors;

import bot.commands.Command;
import bot.commands.interfaces.CommandVisitor;
import bot.commands.interfaces.StartTime;

public class StartTimeVisitor implements CommandVisitor{
    private final long startTime;

    public StartTimeVisitor(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public void visit(Command command) {
        if (command instanceof StartTime) ((StartTime) command).setStartTime(this.startTime);
    }
}

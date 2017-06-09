package bot.commands.visitors;

import bot.commands.Command;
import bot.commands.interfaces.CommandVisitor;
import bot.commands.interfaces.NeedsStartTime;

public class StartTimeVisitor implements CommandVisitor{
    private final long startTime;

    public StartTimeVisitor(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public void visit(Command command) {
        if (command instanceof NeedsStartTime) ((NeedsStartTime) command).setStartTime(this.startTime);
    }
}

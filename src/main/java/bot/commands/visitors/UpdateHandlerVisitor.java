package bot.commands.visitors;

import bot.UpdateHandler;
import bot.commands.Command;
import bot.commands.interfaces.CommandVisitor;
import bot.commands.interfaces.NeedsUpdateHandler;

public class UpdateHandlerVisitor implements CommandVisitor{
    private final UpdateHandler updateHandler;

    public UpdateHandlerVisitor(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    @Override
    public void visit(Command command) {
        if (command instanceof NeedsUpdateHandler) ((NeedsUpdateHandler) command).setUpdateHandler(updateHandler);
    }
}

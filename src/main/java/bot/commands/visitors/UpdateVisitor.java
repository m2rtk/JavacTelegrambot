package bot.commands.visitors;

import bot.commands.Command;
import bot.commands.interfaces.CommandVisitor;
import bot.commands.interfaces.NeedsUpdate;
import org.telegram.telegrambots.api.objects.Update;

public class UpdateVisitor implements CommandVisitor {
    private final Update update;

    public UpdateVisitor(Update update) {
        this.update = update;
    }

    @Override
    public void visit(Command command) {
        if (command instanceof NeedsUpdate) ((NeedsUpdate) command).setUpdate(update);
    }
}

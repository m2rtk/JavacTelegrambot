package bot.commands.parameters;

import bot.commands.Command;
import bot.commands.interfaces.CommandVisitor;

public abstract class Parameter implements CommandVisitor {

    /**
     * Visits command.
     * @param command command to visit.
     */
    public abstract void visit(Command command);

}

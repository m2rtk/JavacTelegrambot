package bot.commands.visitors;

import bot.commands.visitors.Command;
import bot.commands.interfaces.CommandVisitor;

public abstract class Parameter implements CommandVisitor {

    /**
     * Visits command.
     * @param command command to visit.
     */
    public abstract void visit(Command command);

    /**
     * Returns name of parameter.
     * @return Parameter name.
     */
    public abstract String getName();

}

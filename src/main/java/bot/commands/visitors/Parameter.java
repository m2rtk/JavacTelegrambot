package bot.commands.visitors;

import bot.commands.Command;
import bot.commands.interfaces.CommandVisitor;
import bot.commands.parameters.PrivacyParameter;

public abstract class Parameter implements CommandVisitor {

    /**
     * Visits command.
     * @param command command to visit.
     */
    public abstract void visit(Command command);

}

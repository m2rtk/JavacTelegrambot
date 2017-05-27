package bot.commands.interfaces;

import bot.commands.Command;

public interface CommandVisitor {

    /**
     * Visits command.
     * @param command command to visit.
     */
    void visit(Command command);
}

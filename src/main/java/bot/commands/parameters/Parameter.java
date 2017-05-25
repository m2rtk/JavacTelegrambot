package bot.commands.parameters;

import bot.commands.Command;

public abstract class Parameter {

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

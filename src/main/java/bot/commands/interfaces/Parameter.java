package bot.commands.interfaces;

public interface Parameter {

    /**
     * Returns name of parameter.
     * @return Parameter name.
     */
    String getName();

    /**
     * Visits command.
     * @param command command to visit.
     */
    void visit(Command command);
}

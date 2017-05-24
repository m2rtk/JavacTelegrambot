package bot.commands.interfaces;

public interface Parameter {

    /**
     * Returns name of parameter.
     * @return Parameter name.
     */
    String getName();

    /**
     * Returns the number of arguments the parameter takes.
     * @return number of arguments needed.
     */
    int getNrOfParams();

    /**
     * Visits command.
     * @param command command to visit.
     */
    void visit(Command command);
}

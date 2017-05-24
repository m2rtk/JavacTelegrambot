package bot.commands.interfaces;

public interface Command {

    /**
     * Execute command.
     */
    void execute();

    /**
     * Accepts visitor parameter.
     * @param parameterVisitor visitor.
     */
    void acceptParameter(Parameter parameterVisitor);

    /**
     * Returns output after calling execute();
     * Returns null if execute has not been called. todo change it to @throws NullPointerException maybe
     * Currently nice and help command don't need execute() call to return output.
     * @return output as string.
     */
    String getOutput();

    /**
     * Returns name of command.
     * @return Command name.
     */
    String getName();
}

package bot.commands.interfaces;


public abstract class Command {
    /**
     * Accepts visitor parameter.
     * @param parameterVisitor visitor.
     */
    public void acceptParameter(Parameter parameterVisitor) {
        parameterVisitor.visit(this);
    }


    /**
     * Execute command.
     */
    public abstract void execute();

    /**
     * Returns output after calling execute();
     * Returns null if execute has not been called. todo change it to @throws NullPointerException maybe
     * Currently nice and help command don't need execute() call to return output.
     * @return output as string.
     */
    public abstract String getOutput();

    /**
     * Returns name of command.
     * @return Command name.
     */
    public abstract String getName();
}

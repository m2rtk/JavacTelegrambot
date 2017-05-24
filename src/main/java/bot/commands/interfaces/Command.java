package bot.commands.interfaces;

public interface Command {
    void execute();
    void acceptParameter(Parameter parameterVisitor);
    String getOutput();
    String getName();
}

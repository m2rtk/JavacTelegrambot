package bot.commands;

public interface Command {
    void execute();
    String getOutput();
    String getName();
}

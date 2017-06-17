package bot.commands;

public class NiceCommand extends Command {

    @Override
    public void execute() {
        setOutput("nice");
    }

    @Override
    public String toString() {
        return "NiceCommand{}";
    }
}

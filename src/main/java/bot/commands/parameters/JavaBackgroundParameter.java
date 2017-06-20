package bot.commands.parameters;

import bot.JavaBot;
import bot.commands.Command;
import bot.commands.JavaCommand;
import bot.commands.visitors.Parameter;

public class JavaBackgroundParameter extends Parameter{
    private JavaBot bot;

    @Override
    public void visit(Command command) {
        if (command instanceof JavaCommand) {
            ((JavaCommand) command).setRunInBackground(true);
        }
    }
}

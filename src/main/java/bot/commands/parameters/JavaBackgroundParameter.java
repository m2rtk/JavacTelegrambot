package bot.commands.parameters;

import bot.commands.Command;
import bot.commands.JavaCommand;
import bot.commands.visitors.Parameter;

public class JavaBackgroundParameter extends Parameter {

    @Override
    public void visit(Command command) {
        if (command instanceof JavaCommand) {
            ((JavaCommand) command).setRunInBackground(true);
        }
    }
}

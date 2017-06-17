package bot.commands.parameters;

import bot.commands.Command;
import bot.commands.visitors.Parameter;

public class NoMonospaceFontParameter extends Parameter {

    @Override
    public void visit(Command command) {
        command.setMonospaceFont(false);
    }
}

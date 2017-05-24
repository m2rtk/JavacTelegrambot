package bot.commands.parameters;

import bot.commands.Command;
import bot.commands.JavaCommand;

/**
 * Created on 24.05.2017.
 */
public class PrivacyParameter implements Parameter, ParameterVisitor {

    @Override
    public void visit(Command command) {

    }

    @Override
    public String getName() {
        return "-p";
    }

    @Override
    public int getNrOfParams() {
        return 0;
    }
}

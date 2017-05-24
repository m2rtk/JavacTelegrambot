package bot.commands.parameters;

import bot.commands.Command;

public interface ParameterVisitor {
    void visit(Command command);
}

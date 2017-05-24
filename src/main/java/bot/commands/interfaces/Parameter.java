package bot.commands.interfaces;

import bot.commands.interfaces.Command;

public interface Parameter {
    String getName();
    int getNrOfParams();
    void visit(Command command);
}

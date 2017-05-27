package bot.commands.visitors;

import bot.commands.Command;
import bot.commands.interfaces.CommandVisitor;
import bot.commands.interfaces.NeedsDAO;
import dao.BotDAO;

public class DAOVisitor implements CommandVisitor{
    private final BotDAO dao;

    public DAOVisitor(BotDAO dao) {
        this.dao = dao;
    }

    @Override
    public void visit(Command command) {
        if (command instanceof NeedsDAO) ((NeedsDAO) command).setDAO(this.dao);
    }

}

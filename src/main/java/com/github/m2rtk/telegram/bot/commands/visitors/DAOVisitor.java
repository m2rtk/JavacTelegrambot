package com.github.m2rtk.telegram.bot.commands.visitors;

import com.github.m2rtk.telegram.bot.commands.Command;
import com.github.m2rtk.telegram.bot.commands.interfaces.CommandVisitor;
import com.github.m2rtk.telegram.bot.commands.interfaces.NeedsDAO;
import com.github.m2rtk.telegram.dao.BotDAO;

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

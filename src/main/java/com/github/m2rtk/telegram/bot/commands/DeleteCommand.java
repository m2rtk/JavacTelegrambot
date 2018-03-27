package com.github.m2rtk.telegram.bot.commands;

import com.github.m2rtk.telegram.bot.Commands;
import com.github.m2rtk.telegram.bot.commands.interfaces.NeedsArgument;
import com.github.m2rtk.telegram.bot.commands.interfaces.NeedsDAO;
import com.github.m2rtk.telegram.bot.commands.interfaces.NeedsPrivacy;
import com.github.m2rtk.telegram.dao.BotDAO;
import com.github.m2rtk.telegram.dao.Privacy;


public class DeleteCommand extends Command implements NeedsPrivacy, NeedsArgument, NeedsDAO {
    private BotDAO dao;
    private String argument;
    private Privacy privacy;
    private Long id;

    @Override
    public void execute() {
        if (argument == null || argument.isEmpty() || id == null || privacy == null || dao == null) throw new IllegalExecutionException();
        boolean successful = dao.remove(argument, id, privacy);

        if (successful) setOutput("Successfully deleted " + argument);
        else            setOutput("Couldn't delete " + argument);
    }

    @Override
    public String getName() {
        return Commands.delete;
    }

    @Override
    public void setPrivacy(Privacy privacy, Long id) {
        this.privacy = privacy;
        this.id = id;
    }

    @Override
    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public boolean hasArgument() {
        return this.argument != null;
    }

    @Override
    public void setDAO(BotDAO dao) {
        this.dao = dao;
    }

    @Override
    public String toString() {
        return "DeleteCommand{" +
                "com.github.m2rtk.telegram.dao=" + dao +
                ", argument='" + argument + '\'' +
                ", privacy=" + privacy +
                ", id=" + id +
                "} ";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DeleteCommand that = (DeleteCommand) o;

        if (dao != null ? !dao.equals(that.dao) : that.dao != null) return false;
        if (argument != null ? !argument.equals(that.argument) : that.argument != null) return false;
        if (privacy != that.privacy) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dao != null ? dao.hashCode() : 0);
        result = 31 * result + (argument != null ? argument.hashCode() : 0);
        result = 31 * result + (privacy != null ? privacy.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}

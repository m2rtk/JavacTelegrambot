package bot.commands;

import bot.Commands;
import bot.commands.interfaces.Command;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.Private;
import bot.commands.interfaces.Parameter;
import dao.BotDAO;
import dao.Privacy;
import dao.WriteToDiskBotDAO;
import javac.Compiled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static dao.Privacy.CHAT;

/**
 * Created on 18.05.2017.
 */
public class ListCommand implements Command, Private, NeedsDAO {
    private BotDAO dao;
    private Privacy privacy;
    private long id;

    private String output;

    public ListCommand(long id) {
        this.privacy = CHAT;
        this.id = id;
    }

    @Override
    public void execute() {
        StringBuilder sb = new StringBuilder();
        List<String> names = new ArrayList<>();

        if (dao.getAll(id, privacy) != null) {
            names = dao.getAll(id, privacy).stream()
                    .map(Compiled::getName)
                    .collect(Collectors.toList());
            Collections.sort(names);
        }

        sb.append("List: ").append(System.getProperty("line.separator"));
        for (String name : names) sb.append(name).append(System.getProperty("line.separator"));

        output = sb.toString();
    }

    @Override
    public void acceptParameter(Parameter parameterVisitor) {
        parameterVisitor.visit(this);
    }

    @Override
    public String getOutput() {
        return output;
    }

    @Override
    public String getName() {
        return Commands.list;
    }

    @Override
    public void setPrivacy(Privacy privacy, long id) {
        this.privacy = privacy;
        this.id = id;
    }

    @Override
    public void setDAO(BotDAO dao) {
        this.dao = dao;
    }

    @Override
    public String toString() {
        return "ListCommand{" +
                "privacy=" + privacy +
                ", id=" + id +
                ", output='" + output + '\'' +
                '}';
    }
}

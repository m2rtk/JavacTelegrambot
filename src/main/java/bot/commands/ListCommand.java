package bot.commands;

import bot.Commands;
import bot.commands.interfaces.IllegalExecutionException;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.Private;
import dao.BotDAO;
import dao.Privacy;
import javac.Compiled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static dao.Privacy.CHAT;

/**
 * Created on 18.05.2017.
 */
public class ListCommand extends Command implements Private, NeedsDAO {
    private BotDAO dao;
    private Privacy privacy;
    private Long id;

    public ListCommand(Long id) {
        this.privacy = CHAT;
        this.id = id;
    }

    @Override
    public void execute() {
        if (dao == null || privacy == null || id == null) throw new IllegalExecutionException();
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

        setOutput(sb.toString());
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
                "dao=" + dao +
                ", privacy=" + privacy +
                ", id=" + id +
                "} " + super.toString();
    }
}

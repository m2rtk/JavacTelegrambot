package bot.commands;

import bot.Commands;
import dao.BotDAO;
import javac.Compiled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static dao.BotDAO.Privacy;

/**
 * Created on 18.05.2017.
 */
public class ListCommand implements Command {

    private Privacy privacy;
    private BotDAO dao;
    private Long id;

    private String output;

    public ListCommand(Privacy privacy, Long id, BotDAO dao) {
        this.privacy = privacy;
        this.id = id;
        this.dao = dao;
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
    public String getOutput() {
        return output;
    }

    @Override
    public String getName() {
        return Commands.list;
    }
}

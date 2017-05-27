package bot.commands;

import bot.Commands;
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
    public void setPrivacy(Privacy privacy, Long id) {
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
                "} ";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        else if (!(obj instanceof ListCommand)) return false;
        return  ((((ListCommand) obj).dao       == null && this.dao       == null)  || (((ListCommand) obj).dao.equals(this.dao))) &&
                ((((ListCommand) obj).id        == null && this.id        == null)  || (((ListCommand) obj).id.equals(this.id))) &&
                ((((ListCommand) obj).privacy   == null && this.privacy   == null)  || (((ListCommand) obj).privacy.equals(this.privacy)));
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 31 * result + (this.dao       == null ? 0 : this.dao.hashCode()); //dao - as I ever really use 2 different instances of dao, this should be okList     result = 31 * result + (this.id        == null ? 0 : Long.hashCode(this.id)); //id
        result = 31 * result + (this.privacy   == null ? 0 : (this.privacy.equals(CHAT) ? 1 : 0)); //privacy
        return result;
    }
}

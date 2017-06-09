package bot.commands;

import bot.Commands;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.NeedsPrivacy;
import dao.BotDAO;
import dao.Privacy;
import javac.ClassFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 18.05.2017.
 */
public class ListCommand extends Command implements NeedsPrivacy, NeedsDAO {
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
                    .map(ClassFile::getClassName)
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ListCommand that = (ListCommand) o;

        if (dao != null ? !dao.equals(that.dao) : that.dao != null) return false;
        if (privacy != that.privacy) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dao != null ? dao.hashCode() : 0);
        result = 31 * result + (privacy != null ? privacy.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}

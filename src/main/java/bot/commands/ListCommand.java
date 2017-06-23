package bot.commands;

import bot.Utils;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.NeedsPrivacy;
import bot.commands.interfaces.NeedsUpdate;
import dao.BotDAO;
import dao.Privacy;
import javac.BackgroundJavaProcess;
import javac.ClassFile;
import org.telegram.telegrambots.api.objects.Update;

import java.util.*;
import java.util.stream.Collectors;

import static dao.Privacy.CHAT;

/**
 * Created on 18.05.2017.
 */
public class ListCommand extends Command implements NeedsPrivacy, NeedsDAO, NeedsUpdate {
    private BotDAO dao;
    private Privacy privacy = CHAT;
    private Update update;

    private boolean listProcesses = false;

    @Override
    public void execute() {
        if (dao == null || privacy == null || update == null) throw new IllegalExecutionException();
        StringBuilder sb = new StringBuilder();
        Long id = Utils.getId(privacy, update);


        if (listProcesses) {
            Map<Integer, BackgroundJavaProcess> processes = new HashMap<>();
            if (dao.getAllJavaProcesses(id) != null) processes = dao.getAllJavaProcesses(id);
            sb.append("Processes; ").append(System.getProperty("line.separator"));
            for (int key : processes.keySet())
                sb.append(processes.get(key))
                        .append(" ")
                        .append(key)
                        .append(System.getProperty("line.separator"));

        } else {
            List<String> names = new ArrayList<>();
            if (dao.getAll(id, privacy) != null) {
                names = dao.getAll(id, privacy).stream()
                        .map(ClassFile::getClassName)
                        .collect(Collectors.toList());
                Collections.sort(names);
            }
            sb.append("List: ").append(System.getProperty("line.separator"));
            for (String name : names) sb.append(name).append(System.getProperty("line.separator"));
        }


        setOutput(sb.toString());
    }

    public void setListProcesses(boolean val) {
        this.listProcesses = val;
    }

    @Override
    public void setPrivacy(Privacy privacy) {
        this.privacy = privacy;
    }

    @Override
    public void setUpdate(Update update) {
        this.update = update;
    }

    @Override
    public Privacy getPrivacy() {
        return privacy;
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
                ", updateId=" + update.getUpdateId() +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ListCommand that = (ListCommand) o;

        if (listProcesses != that.listProcesses) return false;
        if (dao != null ? !dao.equals(that.dao) : that.dao != null) return false;
        if (privacy != that.privacy) return false;
        return update != null ? update.equals(that.update) : that.update == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dao != null ? dao.hashCode() : 0);
        result = 31 * result + (privacy != null ? privacy.hashCode() : 0);
        result = 31 * result + (update != null ? update.hashCode() : 0);
        result = 31 * result + (listProcesses ? 1 : 0);
        return result;
    }
}

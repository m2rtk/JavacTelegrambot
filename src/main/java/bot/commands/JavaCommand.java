package bot.commands;

import bot.UpdateHandler;
import utils.Utils;
import bot.commands.interfaces.*;
import dao.BotDAO;
import dao.Privacy;
import javac.BackgroundJavaProcess;
import javac.ClassFile;
import javac.Executor;
import org.telegram.telegrambots.api.objects.Update;

import java.util.Arrays;

import static dao.Privacy.CHAT;

public class JavaCommand extends Command implements NeedsArgument, NeedsPrivacy, NeedsDAO, NeedsUpdate, NeedsUpdateHandler {
    private BotDAO dao;
    private String className;
    private String[] args;
    private Privacy privacy = CHAT;
    private Update update;

    private boolean runInBackground = false;
    private UpdateHandler botThread;

    @Override
    public void execute() {
        if (args == null || update == null || privacy == null || dao == null || className == null || className.isEmpty()) throw new IllegalExecutionException();
        Long id = Utils.getId(privacy, update);
        ClassFile classFile = dao.get(className, id, privacy);

        if (classFile == null) {
            setOutput("Database doesn't contain script named '" + className + "'");
            return;
        }

        if (runInBackground) {
            if (botThread == null) throw new IllegalExecutionException();
            BackgroundJavaProcess process = new BackgroundJavaProcess(botThread, dao, classFile, args);
            process.setClassPath(privacy, id);
            process.start();
            dao.addJavaProcess(process, id);
            setOutput("Started " + className + " as a background process with pid " + process.getPid());
        } else {
            Executor executor = new Executor(classFile);
            executor.setClassPath(privacy, id);
            executor.execute(args);
            setOutput(executor.getOutputMessage());
        }
    }

    public void setToRunInBackground() {
        this.runInBackground = true;
    }

    @Override
    public void setUpdateHandler(UpdateHandler botThread) {
        this.botThread = botThread;
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
    public void setArgument(String argument) {
        String[] pieces = argument.split(" ");
        this.className = pieces[0];
        this.args = new String[pieces.length - 1];
        System.arraycopy(pieces, 1, this.args, 0, pieces.length - 1);
    }

    @Override
    public boolean hasArgument() {
        return this.className != null && this.args != null;
    }

    @Override
    public void setDAO(BotDAO dao) {
        this.dao = dao;
    }

    @Override
    public String toString() {
        return "JavaCommand{" +
                "className='" + className + '\'' +
                ", args=" + Arrays.toString(args) +
                ", privacy=" + privacy +
                ", updateId=" + update.getUpdateId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        JavaCommand that = (JavaCommand) o;

        if (runInBackground != that.runInBackground) return false;
        if (dao != null ? !dao.equals(that.dao) : that.dao != null) return false;
        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(args, that.args)) return false;
        if (privacy != that.privacy) return false;
        return update != null ? update.equals(that.update) : that.update == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dao != null ? dao.hashCode() : 0);
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(args);
        result = 31 * result + (privacy != null ? privacy.hashCode() : 0);
        result = 31 * result + (update != null ? update.hashCode() : 0);
        result = 31 * result + (runInBackground ? 1 : 0);
        return result;
    }
}

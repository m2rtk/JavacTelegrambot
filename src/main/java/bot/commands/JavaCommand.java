package bot.commands;

import bot.Commands;
import bot.commands.interfaces.NeedsArgument;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.NeedsPrivacy;
import dao.BotDAO;
import dao.Privacy;
import javac.ClassFile;
import javac.Executor;

import java.util.Arrays;

public class JavaCommand extends Command implements NeedsArgument, NeedsPrivacy, NeedsDAO {
    private BotDAO dao;
    private String className;
    private String[] args;
    private Privacy privacy;
    private Long id;

    @Override
    public void execute() {
        if (args == null || id == null || privacy == null || dao == null || className == null || className.isEmpty()) throw new IllegalExecutionException();
        ClassFile classFile = dao.get(className, id, privacy);

        if (classFile == null) {
            setOutput("Database doesn't contain script named '" + className + "'");
            return;
        }

        Executor executor = new Executor(classFile);
        executor.setClassPath(privacy, id);

        executor.run(args);

        setOutput(executor.getOutputMessage());
    }

    @Override
    public String getName() {
        return Commands.java;
    }

    @Override
    public void setPrivacy(Privacy privacy, Long id) {
        this.privacy = privacy;
        this.id = id;
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
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        JavaCommand that = (JavaCommand) o;

        if (dao != null ? !dao.equals(that.dao) : that.dao != null) return false;
        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        if (!Arrays.equals(args, that.args)) return false;
        if (privacy != that.privacy) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dao != null ? dao.hashCode() : 0);
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(args);
        result = 31 * result + (privacy != null ? privacy.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}

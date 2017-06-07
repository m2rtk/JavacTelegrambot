package bot.commands;

import bot.Commands;
import bot.commands.interfaces.NeedsArgument;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.NeedsPrivacy;
import bot.commands.visitors.Command;
import dao.BotDAO;
import dao.Privacy;
import javac.ClassFile;
import javac.Executor;

import java.util.Arrays;

import static dao.Privacy.CHAT;

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
    public boolean equals(Object obj) {
        if (obj == this) return true;
        else if (!(obj instanceof JavaCommand)) return false;
        return  ((((JavaCommand) obj).dao       == null && this.dao       == null)  || (((JavaCommand) obj).dao.equals(this.dao))) &&
                ((((JavaCommand) obj).className == null && this.className == null)  || (((JavaCommand) obj).className.equals(this.className))) &&
                ((((JavaCommand) obj).args      == null && this.args      == null)  || (Arrays.equals(((JavaCommand) obj).args, this.args))) &&
                ((((JavaCommand) obj).id        == null && this.id        == null)  || (((JavaCommand) obj).id.equals(this.id))) &&
                ((((JavaCommand) obj).privacy   == null && this.privacy   == null)  || (((JavaCommand) obj).privacy.equals(this.privacy)));
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 31 * result + (this.dao       == null ? 0 : this.dao.hashCode()); //dao - as I ever really use 2 different instances of dao, this should be ok
        result = 31 * result + (this.className == null ? 0 : this.className.hashCode()); //className
        result = 31 * result + (this.args      == null ? 0 : Arrays.hashCode(this.args)); //args
        result = 31 * result + (this.id        == null ? 0 : Long.hashCode(this.id)); //id
        result = 31 * result + (this.privacy   == null ? 0 : (this.privacy.equals(CHAT) ? 1 : 0)); //privacy
        return result;
    }
}

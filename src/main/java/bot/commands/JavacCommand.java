package bot.commands;

import bot.Commands;
import bot.commands.interfaces.NeedsArgument;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.NeedsPrivacy;
import bot.commands.visitors.Command;
import dao.BotDAO;
import dao.Privacy;
import javac.Code;

import static dao.Privacy.CHAT;

public class JavacCommand extends Command implements NeedsArgument, NeedsPrivacy, NeedsDAO {
    private BotDAO dao;
    private String content;
    private Privacy privacy;
    private Long id;

    public void wrapContentInMain(String classname) {
        if (content == null) throw new NullPointerException("Content/argument must be set before calling this method.");
        this.content = String.format("public class %s { public static void main(String[] args) {%s}}", classname, content);
    }

    @Override
    public void execute() {
        if (content == null || content.isEmpty() || id == null || privacy == null || dao == null) throw new IllegalExecutionException();
        Code code = new Code(content, privacy, id);

        if (code.compile()) {
            if (dao.get(code.getName(), id, privacy) != null) {
                dao.remove(code.getName(), id, privacy);
            }
            dao.add(code.getCompiled(), id, privacy);
            setOutput("Successfully compiled!");
        } else {
            setOutput("Compilation failed " + System.getProperty("line.separator") + code.getOut());
        }
    }

    @Override
    public String getName() {
        return Commands.javac;
    }

    @Override
    public void setPrivacy(Privacy privacy, Long id) {
        this.privacy = privacy;
        this.id = id;
    }

    @Override
    public void setArgument(String argument) {
        this.content = argument;
    }

    @Override
    public boolean hasArgument() {
        return this.content != null;
    }

    @Override
    public void setDAO(BotDAO dao) {
        this.dao = dao;
    }

    @Override
    public String toString() {
        return "JavacCommand{" +
                "dao=" + dao +
                ", content='" + content + '\'' +
                ", privacy=" + privacy +
                ", id=" + id +
                "} ";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        else if (!(obj instanceof JavacCommand)) return false;
        return  ((((JavacCommand) obj).dao       == null && this.dao       == null)  || (((JavacCommand) obj).dao.equals(this.dao))) &&
                ((((JavacCommand) obj).content   == null && this.content   == null)  || (((JavacCommand) obj).content.equals(this.content))) &&
                ((((JavacCommand) obj).id        == null && this.id        == null)  || (((JavacCommand) obj).id.equals(this.id))) &&
                ((((JavacCommand) obj).privacy   == null && this.privacy   == null)  || (((JavacCommand) obj).privacy.equals(this.privacy)));
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 31 * result + (this.dao       == null ? 0 : this.dao.hashCode()); //dao - as I ever really use 2 different instances of dao, this should be ok
        result = 31 * result + (this.content   == null ? 0 : this.content.hashCode()); //className
        result = 31 * result + (this.id        == null ? 0 : Long.hashCode(this.id)); //id
        result = 31 * result + (this.privacy   == null ? 0 : (this.privacy.equals(CHAT) ? 1 : 0)); //privacy
        return result;
    }
}

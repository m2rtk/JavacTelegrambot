package bot.commands;

import bot.Utils;
import bot.commands.interfaces.NeedsArgument;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.NeedsPrivacy;
import bot.commands.interfaces.NeedsUpdate;
import dao.BotDAO;
import dao.Privacy;
import javac.Compiler;
import javac.JavaFile;
import org.telegram.telegrambots.api.objects.Update;

import static dao.Privacy.CHAT;

public class JavacCommand extends Command implements NeedsArgument, NeedsPrivacy, NeedsDAO, NeedsUpdate {
    private BotDAO dao;
    private String content;
    private Privacy privacy = CHAT;
    private Update update;

    public void wrapContentInMain(String classname) {
        if (content == null) throw new NullPointerException("Content/argument must be set before calling this method.");
        this.content = String.format("public class %s { \n\tpublic static void main(String[] args) {\n\t\t%s\n\t}\n}", classname, content);
    }

    @Override
    public void execute() {
        if (content == null || content.isEmpty() || update == null || privacy == null || dao == null) throw new IllegalExecutionException();
        JavaFile javaFile = new JavaFile(content);
        Compiler compiler = new Compiler(javaFile);
        Long id = Utils.getId(privacy, update);
        compiler.setClassPath(privacy, id);

        if (compiler.compile()) {
            if (dao.get(javaFile.getClassName(), id, privacy) != null) {
                dao.remove(javaFile.getClassName(), id, privacy);
            }
            dao.add(compiler.getOutputClass(), id, privacy);
            setOutput("Successfully compiled!");
        } else {
            setOutput("Compilation failed " + System.getProperty("line.separator") + compiler.getOutputMessage());
        }
    }

    @Override
    public void setUpdate(Update update) {
        this.update = update;
    }

    @Override
    public void setPrivacy(Privacy privacy) {
        this.privacy = privacy;
    }

    @Override
    public Privacy getPrivacy() {
        return privacy;
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
//                ", content='" + content + '\'' +
                ", privacy=" + privacy +
                ", updateId=" + update.getUpdateId() +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        JavacCommand that = (JavacCommand) o;

        if (dao != null ? !dao.equals(that.dao) : that.dao != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (privacy != that.privacy) return false;
        return update != null ? update.equals(that.update) : that.update == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dao != null ? dao.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (privacy != null ? privacy.hashCode() : 0);
        result = 31 * result + (update != null ? update.hashCode() : 0);
        return result;
    }
}

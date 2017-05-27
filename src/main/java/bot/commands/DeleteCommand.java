package bot.commands;

import bot.Commands;
import bot.commands.interfaces.Argument;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.Private;
import dao.BotDAO;
import dao.Privacy;

import static dao.Privacy.CHAT;


public class DeleteCommand extends Command implements Private, Argument, NeedsDAO {
    private BotDAO dao;
    private String argument;
    private Privacy privacy;
    private Long id;

    @Override
    public void execute() {
        if (argument == null || id == null || privacy == null || dao == null) throw new IllegalExecutionException();
        boolean successful = dao.remove(argument, id, privacy);

        if (successful) setOutput("Successfully deleted " + argument);
        else            setOutput("Couldn't delete " + argument);
    }

    @Override
    public String getName() {
        return Commands.delete;
    }

    @Override
    public void setPrivacy(Privacy privacy, Long id) {
        this.privacy = privacy;
        this.id = id;
    }

    @Override
    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public boolean hasArgument() {
        return this.argument != null;
    }

    @Override
    public void setDAO(BotDAO dao) {
        this.dao = dao;
    }

    @Override
    public String toString() {
        return "DeleteCommand{" +
                "dao=" + dao +
                ", argument='" + argument + '\'' +
                ", privacy=" + privacy +
                ", id=" + id +
                "} ";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        else if (!(obj instanceof DeleteCommand)) return false;
        return  ((((DeleteCommand) obj).dao       == null && this.dao       == null)  || (((DeleteCommand) obj).dao.equals(this.dao))) &&
                ((((DeleteCommand) obj).id        == null && this.id        == null)  || (((DeleteCommand) obj).id.equals(this.id))) &&
                ((((DeleteCommand) obj).privacy   == null && this.privacy   == null)  || (((DeleteCommand) obj).privacy.equals(this.privacy)));
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 31 * result + (this.dao       == null ? 0 : this.dao.hashCode()); //dao - as I ever really use 2 different instances of dao, this should be ok
        result = 31 * result + (this.id        == null ? 0 : Long.hashCode(this.id)); //id
        result = 31 * result + (this.privacy   == null ? 0 : (this.privacy.equals(CHAT) ? 1 : 0)); //privacy
        return result;
    }
}

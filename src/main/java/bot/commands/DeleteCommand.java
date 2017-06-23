package bot.commands;

import bot.commands.interfaces.NeedsArgument;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.NeedsPrivacy;
import bot.commands.interfaces.NeedsUpdate;
import dao.BotDAO;
import dao.Privacy;
import org.telegram.telegrambots.api.objects.Update;

import static dao.Privacy.CHAT;


public class DeleteCommand extends Command implements NeedsPrivacy, NeedsArgument, NeedsDAO, NeedsUpdate {
    private BotDAO dao;
    private String argument;
    private Privacy privacy = CHAT;
    private Long id;
    private Update update;

    @Override
    public void execute() {
        if (argument == null || argument.isEmpty() || update == null || privacy == null || dao == null) throw new IllegalExecutionException();
        Long id = privacy == CHAT ? update.getMessage().getChatId() : update.getMessage().getFrom().getId() ;
        boolean successful = dao.remove(argument, id, privacy);

        if (successful) setOutput("Successfully deleted " + argument);
        else            setOutput("Couldn't delete " + argument);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DeleteCommand that = (DeleteCommand) o;

        if (dao != null ? !dao.equals(that.dao) : that.dao != null) return false;
        if (argument != null ? !argument.equals(that.argument) : that.argument != null) return false;
        if (privacy != that.privacy) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dao != null ? dao.hashCode() : 0);
        result = 31 * result + (argument != null ? argument.hashCode() : 0);
        result = 31 * result + (privacy != null ? privacy.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}

package bot.commands.parameters;

import bot.Commands;
import bot.commands.Command;
import bot.commands.interfaces.Private;
import dao.Privacy;

import static dao.Privacy.CHAT;

public class PrivacyParameter extends Parameter implements Private {
    private Long id;
    private Privacy privacy;

    @Override
    public void visit(Command command) {
        if (command instanceof Private) ((Private) command).setPrivacy(privacy, id);
    }

    @Override
    public String getName() {
        return Commands.privacyParameter;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        else if (!(obj instanceof PrivacyParameter)) return false;
        return  ((((PrivacyParameter) obj).id      == null && this.id      == null) || (((PrivacyParameter) obj).id.equals(this.id))) &&
                ((((PrivacyParameter) obj).privacy == null && this.privacy == null) || (((PrivacyParameter) obj).privacy.equals(this.privacy)));
    }

    @Override
    public int hashCode() {
        int result = 11;
        result = 31 * result + (this.id      == null ? 0 : Long.hashCode(this.id));
        result = 31 * result + (this.privacy == null ? 0 : (this.privacy == CHAT ? 1 : 0));
        return result;
    }

    @Override
    public void setPrivacy(Privacy privacy, Long id) {
        this.privacy = privacy;
        this.id = id;
    }

    public PrivacyParameter set(Privacy privacy, Long id) {
        this.privacy = privacy;
        this.id = id;
        return this;
    }
}

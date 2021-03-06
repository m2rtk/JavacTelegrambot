package com.github.m2rtk.telegram.bot.commands.parameters;

import com.github.m2rtk.telegram.bot.Commands;
import com.github.m2rtk.telegram.bot.commands.Command;
import com.github.m2rtk.telegram.bot.commands.interfaces.NeedsPrivacy;
import com.github.m2rtk.telegram.bot.commands.visitors.Parameter;
import com.github.m2rtk.telegram.dao.Privacy;

public class PrivacyParameter extends Parameter implements NeedsPrivacy {
    private Long id;
    private Privacy privacy;

    @Override
    public void visit(Command command) {
        if (command instanceof NeedsPrivacy) ((NeedsPrivacy) command).setPrivacy(privacy, id);
    }

    @Override
    public String getName() {
        return Commands.privacyParameter;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrivacyParameter that = (PrivacyParameter) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return privacy == that.privacy;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (privacy != null ? privacy.hashCode() : 0);
        return result;
    }
}

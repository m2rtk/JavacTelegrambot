package bot.commands.interfaces;

import org.telegram.telegrambots.api.objects.Update;

public interface NeedsUpdate {
    void setUpdate(Update update);
}

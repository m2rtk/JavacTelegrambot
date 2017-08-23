import bot.JavaBot;
import bot.UpdateHandler;
import dao.InMemoryBotDAO;
import org.junit.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

public class UpdateHandlerTests {
    private static final Long chat = 0L;
    private static final Long user = 0L;

    private static final JavaBot bot = new JavaBot(new InMemoryBotDAO());

    @Test
    public void ignoresNonCommands() {
        UpdateHandler uh = spy(new UpdateHandler(Utils.createMockUpdate("test", chat, user), bot));
        doThrow(new RuntimeException("This method was not supposed to be called.")).when(uh).sendMessage(anyString());
        uh.run();
    }

    @Test(expected = RuntimeException.class)
    public void executesCommands() {
        UpdateHandler uh = spy(new UpdateHandler(Utils.createMockUpdate("/test", chat, user), bot));
        doThrow(new RuntimeException("Pass.")).when(uh).sendMessage(anyString());
        uh.run();
    }

}

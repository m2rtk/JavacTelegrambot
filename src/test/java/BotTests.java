import bot.BotMcBotfaceBot;
import dao.BotDAO;
import dao.InMemoryBotDAO;
import org.junit.Before;
import org.junit.Test;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import static dao.BotDAO.Privacy.CHAT;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BotTests {
    private static final BotMcBotfaceBot bot = new BotMcBotfaceBot();
    private static BotDAO dao;

    @Before
    public void init() throws Exception {
        dao = changeDAO(bot);
    }
    @Test
    public void receivesUpdateTest() throws Exception {
        BotMcBotfaceBot botmock = mock(BotMcBotfaceBot.class);
        Message mockMessage = mock(Message.class);
        when(mockMessage.getChatId()).thenReturn(1L);
        Update mockUpdate = mock(Update.class);
        when(mockUpdate.getMessage()).thenReturn(mockMessage);
        botmock.onUpdateReceived(mockUpdate);
        verify(botmock, times(1)).onUpdateReceived(mockUpdate);
    }

    @Test
    public void compileHelloWorldTest() throws Exception {
        compileAndSaveValidCodeTest("HelloWorld");
    }

    @Test
    public void compileM8Test() throws Exception {
        compileAndSaveValidCodeTest("M8");
    }

    // TODO: 13.04.2017 make better
    private void compileAndSaveValidCodeTest(String sourceName) throws Exception {
        String source = String.join("\n", Files.readAllLines(Paths.get("src/test/resources/src/" + sourceName + ".java")).stream().collect(Collectors.toList()));
        source = "/javac " + source;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1);
        Message mockMessage = mock(Message.class);
        when(mockMessage.getChatId()).thenReturn(-1L);
        when(mockMessage.isCommand()).thenReturn(true);
        when(mockMessage.getText()).thenReturn(source);
        when(mockMessage.getFrom()).thenReturn(mockUser);
        Update mockUpdate = mock(Update.class);
        when(mockUpdate.hasMessage()).thenReturn(true);
        when(mockUpdate.getMessage()).thenReturn(mockMessage);

        bot.onUpdateReceived(mockUpdate);
        byte[] targetByteCode = Files.readAllBytes(Paths.get("src/test/resources/out/" + sourceName + ".class"));

        assertTrue(dao.contains(sourceName, -1L, CHAT));
        assertTrue(Arrays.equals(targetByteCode, (dao.get(sourceName, -1L, CHAT).getByteCode())));
    }

    private BotDAO changeDAO(BotMcBotfaceBot bot) throws Exception {
        BotDAO dao = new InMemoryBotDAO();

        Field field = BotMcBotfaceBot.class.getDeclaredField("dao");
        field.setAccessible(true);
        field.set(bot, dao);

        return dao;
    }
}

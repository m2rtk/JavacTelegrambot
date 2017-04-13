import bot.BotMcBotfaceBot;
import dao.BotDAO;
import javac.Compiled;
import org.junit.Before;
import org.junit.Test;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.Arrays;

import static dao.BotDAO.Privacy.CHAT;
import static dao.BotDAO.Privacy.USER;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BotTests {
    private static final BotMcBotfaceBot bot = new BotMcBotfaceBot();
    private static final Long CHAT_1 = -1L;
    private static final Long CHAT_2 = -2L;
    private static final Long USER_1 =  1L;
    private static final Long USER_2 =  2L;
    private static BotDAO dao;

    @Before
    public void init() throws Exception {
        dao = Utils.changeDAO(bot);
        byte[] bytecode = Utils.readOut("Test");
        dao.add(new Compiled(bytecode, "Test", CHAT, CHAT_2));
        dao.add(new Compiled(bytecode, "Test", USER, USER_2));
    }

    @Test
    public void receivesUpdateTest() throws Exception {
        BotMcBotfaceBot botmock = mock(BotMcBotfaceBot.class);
        Message mockMessage = mock(Message.class);
        when(mockMessage.getChatId()).thenReturn(USER_1);
        Update mockUpdate = mock(Update.class);
        when(mockUpdate.getMessage()).thenReturn(mockMessage);
        botmock.onUpdateReceived(mockUpdate);
        verify(botmock, times(1)).onUpdateReceived(mockUpdate);
    }

    @Test
    public void javacNoParamsTest1() throws Exception {
        javacTest("M8");
    }

    @Test
    public void javacNoParamsTest2() throws Exception {
        javacTest("HelloWorld");
    }

    @Test
    public void javacWith_m_ParamTest() throws Exception {
        String name = "HelloWorld";
        String content = "/javac -m HelloWorld System.out.println(\"Hello World!\");";
        Update update = Utils.createMockUpdateWithTextContent(content, USER_1, CHAT_1);
        bot.onUpdateReceived(update);

        assertTrue(dao.contains(name, CHAT_1, CHAT));
    }

    @Test
    public void javacWith_p_ParamTest() throws Exception {
        String name = "HelloWorld";
        String content = "/javac -p " + Utils.readSource(name);

        byte[] targetByteCode = Utils.readOut(name);
        Update update = Utils.createMockUpdateWithTextContent(content, USER_1, CHAT_1);
        bot.onUpdateReceived(update);

        assertTrue(dao.contains(name, USER_1, USER));
        assertTrue(Arrays.equals(targetByteCode, (dao.get(name, USER_1, USER).getByteCode())));
    }

    @Test
    public void javacWith_m_p_ParamsWorldTest() throws Exception {
        String name = "HelloWorld";
        String content = "/javac -m HelloWorld -p System.out.println(\"Hello World!\");";

        Update update = Utils.createMockUpdateWithTextContent(content, USER_1, CHAT_1);
        bot.onUpdateReceived(update);

        assertTrue(dao.contains(name, USER_1, USER));
    }

    @Test
    public void deleteCodeChatTest() throws Exception {
        assertTrue(dao.contains("Test", USER_2, USER));
        assertTrue(dao.contains("Test", CHAT_2, CHAT));
        String content = "/delete -p Test";
        Update update = Utils.createMockUpdateWithTextContent(content, USER_2, CHAT_2);
        bot.onUpdateReceived(update);

        assertTrue(!dao.contains("Test", USER_2, USER));
        assertTrue( dao.contains("Test", CHAT_2, CHAT));
    }

    @Test
    public void deleteCodeUserTest() throws Exception {
        assertTrue(dao.contains("Test", USER_2, USER));
        assertTrue(dao.contains("Test", CHAT_2, CHAT));
        String content = "/delete Test";
        Update update = Utils.createMockUpdateWithTextContent(content, USER_2, CHAT_2);
        bot.onUpdateReceived(update);

        assertTrue( dao.contains("Test", USER_2, USER));
        assertTrue(!dao.contains("Test", CHAT_2, CHAT));
    }

    private void javacTest(String sourceName) throws Exception {
        String content = "/javac " + Utils.readSource(sourceName);
        byte[] targetByteCode = Utils.readOut(sourceName);
        Update update = Utils.createMockUpdateWithTextContent(content, USER_1, CHAT_1);
        bot.onUpdateReceived(update);

        assertTrue(dao.contains(sourceName, CHAT_1, CHAT));
        assertTrue(Arrays.equals(targetByteCode, (dao.get(sourceName, CHAT_1, CHAT).getByteCode())));
    }
}

import bot.JavaBot;
import dao.BotDAO;
import javac.Compiled;
import org.junit.Before;
import org.junit.Test;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.logging.BotLogger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;

import static dao.BotDAO.Privacy;
import static dao.BotDAO.Privacy.CHAT;
import static dao.BotDAO.Privacy.USER;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BotTests {
    private static final JavaBot bot = new JavaBot();
    private static final Long CHAT_1 = -1L;
    private static final Long CHAT_2 = -2L;
    private static final Long USER_1 =  1L;
    private static final Long USER_2 =  2L;
    private static final String TEST_LOG = "test.log";
    private static BotDAO dao;

    @Before
    public void init() throws Exception {
        dao = Utils.changeDAO(bot);
        byte[] bytecode = Utils.readOut("Test");
        dao.add(new Compiled(bytecode, "Test"), USER_2, USER);
        dao.add(new Compiled(bytecode, "Test"), CHAT_2, CHAT);
        Handler handler = new FileHandler(TEST_LOG);
        handler.setFormatter(new SimpleFormatter());
        BotLogger.registerLogger(handler);
    }

    @Test
    public void receivesUpdateTest() throws Exception {
        JavaBot mockBot = mock(JavaBot.class);
        Message mockMessage = mock(Message.class);
        when(mockMessage.getChatId()).thenReturn(USER_1);
        Update mockUpdate = mock(Update.class);
        when(mockUpdate.getMessage()).thenReturn(mockMessage);
        mockBot.onUpdateReceived(mockUpdate);
        verify(mockBot, times(1)).onUpdateReceived(mockUpdate);
    }

    @Test
    public void javacNoParamsTest1() throws Exception {
        javacNoParamsTest("M8");
    }

    @Test
    public void javacNoParamsTest2() throws Exception {
        javacNoParamsTest("HelloWorld");
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

    @Test
    public void javaCodeChatSuccessTest() throws Exception {
        javaTest("Test", new String[0], CHAT, USER_1, CHAT_2);
    }

    @Test
    public void javaCodeChatFailTest() throws Exception {
        javaTest("Test", new String[0], CHAT, USER_1, CHAT_1);
        testLogContains("Database doesn't contain script named 'Test'");
    }

    @Test
    public void javaCodeUserSuccessTest() throws Exception {
        javaTest("Test", new String[0], USER, USER_2, CHAT_1);
    }

    @Test
    public void javaCodeChatFailTest1() throws Exception {
        javaTest("Test", new String[0], USER, USER_1, CHAT_2);
        testLogContains("Database doesn't contain script named 'Test'");
    }

    @Test
    public void javaCodeChatFailTest2() throws Exception {
        javaTest("Test", new String[0], USER, USER_1, CHAT_1);
        testLogContains("Database doesn't contain script named 'Test'");
    }

    @Test
    public void javaCodeWithArgumentsTest() throws Exception {
        javaTest("Test", new String[]{"wow"}, USER, USER_2, CHAT_2);
        testLogContains("Sending message 'wow"); // is enough
    }

    private void setCorrectTestClasspaths() throws Exception {
        String path = getClass().getClassLoader().getResource("out/").getPath();
        Utils.setObjectField(dao.get("Test", CHAT_2, CHAT), "classPath", path);
        Utils.setObjectField(dao.get("Test", USER_2, USER), "classPath", path);
    }

    private void javaTest(String sourceName, String[] args, Privacy privacy, Long user, Long chat) throws Exception {
        String content = "/java ";
        if (privacy == USER) content += "-p ";
        content += sourceName;
        for (String arg : args) content += " " + arg;
        Update update = Utils.createMockUpdateWithTextContent(content, user, chat);

        setCorrectTestClasspaths();
        bot.onUpdateReceived(update);

        String expectedOutput = "Executing Java command in chat: " + chat
                + " with privacy: " + privacy + " className: " + sourceName + " args: " + Arrays.asList(args);

        testLogContains(expectedOutput);
    }

    private void javacNoParamsTest(String sourceName) throws Exception {
        String content = "/javac " + Utils.readSource(sourceName);
        byte[] targetByteCode = Utils.readOut(sourceName);
        Update update = Utils.createMockUpdateWithTextContent(content, USER_1, CHAT_1);
        bot.onUpdateReceived(update);

        assertTrue(dao.contains(sourceName, CHAT_1, CHAT));
        assertTrue(Arrays.equals(targetByteCode, (dao.get(sourceName, CHAT_1, CHAT).getByteCode())));
    }

    // I feel that this is a bad way of doing things but it is what it is
    // TODO: 14.04.2017 find a better solution to do this
    private void testLogContains(String expected) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(TEST_LOG));
        boolean passed = false;
        for (String line : lines) {
            if (line.contains(expected)) {
                passed = true;
                break;
            }
        }
        assertTrue("Log didn't contain String '" + expected + "'", passed);
    }
}

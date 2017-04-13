import bot.BotMcBotfaceBot;
import dao.BotDAO;
import dao.InMemoryBotDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;
import java.util.stream.Collectors;

import static dao.BotDAO.Privacy.CHAT;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class BotTests {
    private static final String TEST_LOG = "test.log";
    private static final BotMcBotfaceBot bot = new BotMcBotfaceBot();
    private static BotDAO dao;

    @Before
    public void init() throws Exception {
        Handler handler = new FileHandler("test.log");
        handler.setFormatter(new SimpleFormatter());
        BotLogger.registerLogger(handler);

        dao = changeDAO(bot);
    }

//    @After
//    public void end() throws IOException {
//        Files.delete(Paths.get(TEST_LOG));
//    }

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
        compilesValidCodeTest("HelloWorld");
    }

    @Test
    public void compileM8Test() throws Exception {
        compilesValidCodeTest("M8");
    }

    // TODO: 13.04.2017 make better
    private void compilesValidCodeTest(String sourceName) throws Exception {
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

    /**
     * Uses reflection to create objects with custom private field values.
     */
    private Object createCustomObject(Class object, Map<String, Object> fields) throws Exception {
        Object obj = object.newInstance();
        for (String fieldName : fields.keySet()) {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, fields.get(fieldName));
        }
        return obj;
    }

    private void testLogContains(String expected) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(TEST_LOG));
            boolean passed = false;
            for (String line : lines) {
                if (line.contains(expected)) {
                    passed = true;
                    break;
                }
            }
            assertTrue("Log didn't contain String '" + expected + "'", passed);
        } catch (IOException e) {
            fail("Problem reading from test log file");
        }
    }

    private BotDAO changeDAO(BotMcBotfaceBot bot) throws Exception {
        BotDAO dao = new InMemoryBotDAO();

        Field field = BotMcBotfaceBot.class.getDeclaredField("dao");
        field.setAccessible(true);
        field.set(bot, dao);

        return dao;
    }
}

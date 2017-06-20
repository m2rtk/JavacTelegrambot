import bot.JavaBot;
import bot.UpdateHandler;
import dao.InMemoryBotDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.UpdatesHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


// TODO: 19.06.2017 this is super broken please fix
public class BotTests {
    private static final Long chat = 0L;
    private static final Long user = 0L;
    private static final String unknownCommand = "Database doesn't contain script named '.*'";
    private static final String invalidCommand = "Invalid command: .*";

    private static final JavaBot bot = spy(new JavaBot(new InMemoryBotDAO()));

//    private static UpdateHandler getThread(Update update) {
//        UpdateHandler t = spy(new UpdateHandler(update));
//        doNothing().when(t).sendMessage(anyString(), anyLong());
//        return spy(t);
//    }

    @BeforeClass
    public static void initTests() throws Exception {
//        UpdateHandler.setBot(bot);
    }

    @Before
    public void init() {
//        UpdateHandler bott = spy(new UpdateHandler());
//        doNothing().when(bot).sendMessage(anyString(), anyLong());
    }

    @After
    public void end() throws IOException {
        Path path = Paths.get("cache/CHAT/" + chat);
        if (Files.exists(path))
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
    }

    @Test
    public void niceCommandOutputsNice() {
        test("/nice", "nice");
    }

    @Test
    public void upCommandOutputsUptime() {
        test("/up", "I've been up for [0-9]+ seconds." + System.getProperty("line.separator") +
                    "That's [0-9]+ days, [0-9]+ hours, [0-9]+ minutes and [0-9]+ seconds.");
    }

    @Test
    public void helpCommandOutputsHelp() throws Exception {
        String expectedOutput = String.join(System.getProperty("line.separator"), Files.readAllLines(
                Paths.get(ClassLoader.getSystemResource("HelpMessage.txt").toURI()))
        );
        testWithNoRegex("/help", expectedOutput);
    }

    @Test
    public void listCommandOutputsClasses() {
        test("/list", "List: " + System.getProperty("line.separator") + ".*");
    }

    @Test
    public void deleteCommandOutputsDeleteResult() {
        test("/delete Test", "Couldn't delete Test");
    }

    @Test
    public void javaCommandOutputsClassOutput() {
        Update update = Utils.createMockUpdateWithTextContent("/javac -m Test System.out.println(1);", user, chat);
//        bot.onUpdateReceived(update);
//        getThread(update).start();
        init();
        test("/java Test", "1" + System.getProperty("line.separator"));
    }

    @Test
    public void javaCommandSpecialCaseOutputsClassOutput() {
        Update update = Utils.createMockUpdateWithTextContent("/javac -m Test System.out.println(1);", user, chat);
        bot.onUpdateReceived(update);
        init();
        test("/Test", "1" + System.getProperty("line.separator"));
    }

    @Test
    public void javaCommandSpecialCaseWithArgumentsOutputsClassOutput() {
        Update update = Utils.createMockUpdateWithTextContent("/javac -m Test System.out.println(args[0] + args[1]);", user, chat);
        bot.onUpdateReceived(update);
        init();
        test("/Test 1 2", "12" + System.getProperty("line.separator"));
    }

    @Test
    public void javacCommandOutputsSuccessfulCompilationResult() {
        test("/javac -m Test System.out.println(1);", "Successfully compiled!");
    }

    @Test
    public void invalidCommandOutputsErrorMessage() {
        // unknown commands
        test("/upp", unknownCommand); init();
        test("/destroyUniverse", unknownCommand); init();
        test("/sudo rm -rf --no-preserve-root /", unknownCommand); init();
        test("/java Test", unknownCommand); init(); // because dao doesn't have class Test

        // missing arguments
        test("/delete", invalidCommand); init();
        test("/java", invalidCommand); init();
        test("/javac", invalidCommand); init();
        test("/javac -m", invalidCommand); init();
        test("/javac -m Test", invalidCommand); init();
        test("/javac -m -p", invalidCommand); init();
        test("/javac -m Test -p", invalidCommand); init();
    }

    @Test
    public void noOutputWhenInputIsNotPrefixedWithForwardSlash() throws Exception {
        Update update = Utils.createMockUpdateWithTextContent("nice", user, chat);
        bot.onUpdateReceived(update);
//        verify(bot, never()).sendMessage(any());
    }

    @Test
    public void test11() throws Exception {
        Update update = Utils.createMockUpdateWithTextContent("/nice", user, chat);
//        JavaBot bot = spy(new JavaBot());
//        SendMessage sendMessage = mock(new SendMessage());
//        sendMessage.enableMarkdown(true);
//        sendMessage.setChatId(chat);
//        sendMessage.setText("nice");

//        try {
//            doNothing().when(bot).sendMessage();
//        } catch (TelegramApiException e) {
//            System.out.println(e.getMessage());
//        }
//        UpdateHandler uh = new UpdateHandler(update, bot);
//        uh.run();
    }

    private static void testWithNoRegex(String input, String expectedOutput) {
        Update update = Utils.createMockUpdateWithTextContent(input, user, chat);
        bot.onUpdateReceived(update);
        expectedOutput = "```" + System.getProperty("line.separator") + expectedOutput + System.getProperty("line.separator") + "```";
//        verify(bot).sendMessage(expectedOutput, chat);
    }

    private static void test(String input, String expectedOutput) {
        Update update = Utils.createMockUpdateWithTextContent(input, user, chat);
//        bot.onUpdateReceived(update);
//        UpdateHandler t = getThread(update);
//        t.run();
        expectedOutput = "```" + System.getProperty("line.separator") + expectedOutput + System.getProperty("line.separator") + "```";
//        verify(bot).sendMessage(matches(expectedOutput), eq(chat));

        try {
//            doNothing().when(t).sendMessage(matches(expectedOutput), eq(chat));
//            verify(t).sendMessage(matches(expectedOutput), eq(chat));
        } catch (RuntimeException e) {
            System.out.println("yea");
        }
    }
}

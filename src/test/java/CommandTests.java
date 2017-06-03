import bot.Commands;
import bot.commands.*;
import dao.BotDAO;
import dao.InMemoryBotDAO;
import javac.Compiled;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dao.Privacy.CHAT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommandTests {
    private BotDAO dao;
    private static final Long CHAT_1 = -1L;

    private static Compiled c1;
    private static Compiled c2;
    private static Compiled c3;

    static  {
        try {
            c1 = new Compiled(Utils.readOut("Print"), "Print"); // 1 arg
            c2 = new Compiled(Utils.readOut("Sum"),  "Sum");  // 2 args
            c3 = new Compiled(Utils.readOut("M8"),   "M8");   // * args
        } catch (Exception ignored) {}
    }

    @Before
    public void init() {
        this.dao = new InMemoryBotDAO();
        dao.add(c1, CHAT_1, CHAT);
        dao.add(c2, CHAT_1, CHAT);
        dao.add(c3, CHAT_1, CHAT);
    }

    @Test
    public void upCommandTest() {
        UpCommand upCommand = new UpCommand();
        upCommand.setStartTime(0L);
        upCommand.execute();

        String pattern = "I've been up for [0-9]+ seconds.";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(upCommand.getOutput());
        assertTrue(m.find());
        assertEquals(Commands.up, upCommand.getName());
    }

    @Test(expected = IllegalExecutionException.class)
    public void upCommandIllegalExecutionTest() {
        new UpCommand().execute();
    }

    @Test
    public void niceCommandTest() {
        NiceCommand niceCommand = new NiceCommand();
        niceCommand.execute();

        assertEquals("nice", niceCommand.getOutput());
        assertEquals(Commands.nice, niceCommand.getName());
    }

    @Test
    public void helpCommandTest() throws Exception {
        HelpCommand helpCommand = new HelpCommand();
        helpCommand.execute();

        String expectedOutput = String.join("\n", Files.readAllLines(
                Paths.get(ClassLoader.getSystemResource("HelpMessage.txt").toURI()))
        );

        assertEquals(expectedOutput, helpCommand.getOutput());
        assertEquals(Commands.help, helpCommand.getName());
    }

    @Test
    public void listCommandTest() {
        ListCommand listCommand = new ListCommand();
        listCommand.setPrivacy(CHAT, CHAT_1);
        listCommand.setDAO(dao);
        listCommand.execute();

        String expectedOutput = "List: " + System.getProperty("line.separator");
        expectedOutput += "M8" + System.getProperty("line.separator");
        expectedOutput += "Print" + System.getProperty("line.separator");
        expectedOutput += "Sum" + System.getProperty("line.separator");

        assertEquals(expectedOutput, listCommand.getOutput());
        assertEquals(Commands.list, listCommand.getName());
    }

    @Test(expected = IllegalExecutionException.class)
    public void listCommandIllegalExecutionTest() {
        new ListCommand().execute();
    }

    @Test
    public void deleteCommandTest() {
        assertTrue(dao.getAll(CHAT_1, CHAT).size() == 3);
        DeleteCommand deleteCommand = new DeleteCommand();
        deleteCommand.setPrivacy(CHAT, CHAT_1);
        deleteCommand.setDAO(dao);
        deleteCommand.setArgument("Print");
        deleteCommand.execute();

        String expectedOutput = "Successfully deleted Print";

        assertEquals(expectedOutput, deleteCommand.getOutput());
        assertEquals(Commands.delete, deleteCommand.getName());

        assertTrue(dao.getAll(CHAT_1, CHAT).size() == 2);
        assertTrue(!dao.contains("Print", CHAT_1, CHAT));
        assertTrue( dao.contains("Sum", CHAT_1, CHAT));
        assertTrue( dao.contains("M8", CHAT_1, CHAT));
    }

    @Test(expected = IllegalExecutionException.class)
    public void deleteCommandIllegalExecutionTest() {
        new DeleteCommand().execute();
    }

    @Test
    public void javaCommandTest() throws Exception {
        JavaCommand javaCommand = new JavaCommand();
        javaCommand.setPrivacy(CHAT, CHAT_1);
        javaCommand.setDAO(dao);
        javaCommand.setArgument("Print");
        javaCommand.execute();

        // error because the command tries to execute Test in cache/CHAT/CHAT_1 folder which is empty
        // todo redirect cache folder for tests or something like that
        String expectedOutput = "Error: Could not find or load main class Print" + System.getProperty("line.separator");

        assertEquals(expectedOutput, javaCommand.getOutput());
        assertEquals(Commands.java, javaCommand.getName());
    }

    @Test(expected = IllegalExecutionException.class)
    public void javaCommandIllegalExecutionTest() {
        new JavaCommand().execute();
    }

    @Test
    public void javacCommandTest() {
        JavacCommand javacCommand = new JavacCommand();
        javacCommand.setPrivacy(CHAT, CHAT_1);
        javacCommand.setDAO(dao);
        javacCommand.setArgument("System.out.println(1);");
        javacCommand.wrapContentInMain("Test4");
        javacCommand.execute();

        String expectedOutput = "Successfully compiled!";

        assertEquals(expectedOutput, javacCommand.getOutput());
        assertEquals(Commands.javac, javacCommand.getName());
    }

    @Test(expected = IllegalExecutionException.class)
    public void javacCommandIllegalExecutionTest() {
        new JavaCommand().execute();
    }

}

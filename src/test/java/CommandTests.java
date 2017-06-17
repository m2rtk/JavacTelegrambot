import bot.commands.*;
import bot.commands.interfaces.NeedsArgument;
import bot.commands.interfaces.NeedsDAO;
import bot.commands.interfaces.NeedsPrivacy;
import dao.BotDAO;
import dao.InMemoryBotDAO;
import dao.Privacy;
import javac.ClassFile;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dao.Privacy.CHAT;
import static org.junit.Assert.*;

public class CommandTests {
    private BotDAO dao;
    private static final Long CHAT_1 = -1L;

    private static ClassFile print;
    private static ClassFile sum;
    private static ClassFile M8;

    static  {
        try {
            print      = Utils.readClassFile("Print");
            sum        = Utils.readClassFile("Sum");
            M8         = Utils.readClassFile("M8");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load compiled from out.");
        }
    }

    @Before
    public void init() {
        dao = new InMemoryBotDAO();
        dao.add(print, CHAT_1, CHAT);
        dao.add(sum, CHAT_1, CHAT);
        dao.add(M8, CHAT_1, CHAT);
    }

    @Test
    public void upCommandWithCorrectContentOutputsUpTime() {
        UpCommand upCommand = new UpCommand(){{
            setStartTime(0L);
        }};
        upCommand.execute();

        String pattern = "I've been up for [0-9]+ seconds.";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(upCommand.getOutput());
        assertTrue(m.find());
    }

    @Test
    public void upCommandWithMissingContentThrowsException() {
        illegalExecutionTest(UpCommand.class, false, false, false);
    }

    @Test
    public void niceCommandOutputsNice() {
        NiceCommand niceCommand = new NiceCommand();
        niceCommand.setMonospaceFont(false);
        niceCommand.execute();

        assertEquals("nice", niceCommand.getOutput());
    }

    @Test
    public void helpCommandOutputsHelp() throws Exception {
        HelpCommand helpCommand = new HelpCommand();
        helpCommand.execute();

        String expectedOutput = String.join(System.getProperty("line.separator"), Files.readAllLines(
                Paths.get(ClassLoader.getSystemResource("HelpMessage.txt").toURI()))
        );

        expectedOutput = "```" + System.getProperty("line.separator") + expectedOutput + System.getProperty("line.separator") + "```";

        assertEquals(expectedOutput, helpCommand.getOutput());
    }

    @Test
    public void listCommandWithCorrectContentOutputsListOfMethods() {
        ListCommand listCommand = new ListCommand(){{
            setPrivacy(CHAT);
            setId(CHAT_1);
            setDAO(dao);
            setMonospaceFont(false);
        }};
        listCommand.execute();

        // TODO: 07.06.2017 Compare sets instead of strings
        String expectedOutput = "List: " + System.getProperty("line.separator");
        expectedOutput += "M8" + System.getProperty("line.separator");
        expectedOutput += "Print" + System.getProperty("line.separator");
        expectedOutput += "Sum" + System.getProperty("line.separator");

        assertEquals(expectedOutput, listCommand.getOutput());
    }

    @Test
    public void listCommandWithMissingContentThrowsException() {
        illegalExecutionTest(ListCommand.class, false, false, false);
        illegalExecutionTest(ListCommand.class, false, true, false);
        illegalExecutionTest(ListCommand.class, true, false, false);
    }

    @Test
    public void deleteCommandWithCorrectContentDeletesMethod() {
        assertTrue(dao.getAll(CHAT_1, CHAT).size() == 3);
        DeleteCommand deleteCommand = new DeleteCommand(){{
            setPrivacy(CHAT);
            setId(CHAT_1);
            setDAO(dao);
            setArgument("Print");
            setMonospaceFont(false);
        }};
        deleteCommand.execute();

        String expectedOutput = "Successfully deleted Print";

        assertEquals(expectedOutput, deleteCommand.getOutput());

        assertTrue(dao.getAll(CHAT_1, CHAT).size() == 2);
        assertTrue(!dao.contains("Print", CHAT_1, CHAT));
        assertTrue( dao.contains("Sum", CHAT_1, CHAT));
        assertTrue( dao.contains("M8", CHAT_1, CHAT));
    }

    @Test(expected = IllegalExecutionException.class)
    public void deleteCommandWithEmptyArgumentThrowsException() {
        DeleteCommand deleteCommand = new DeleteCommand(){{
            setPrivacy(CHAT);
            setId(CHAT_1);
            setDAO(dao);
            setArgument("");
        }};
        deleteCommand.execute();
    }


    @Test
    public void deleteCommandWithMissingContentThrowsException() {
        illegalExecutionTest(DeleteCommand.class, true, true, false);
        illegalExecutionTest(DeleteCommand.class, true, false, true);
        illegalExecutionTest(DeleteCommand.class, false, true, true);
        illegalExecutionTest(DeleteCommand.class, false, false, true);
        illegalExecutionTest(DeleteCommand.class, false, true, false);
        illegalExecutionTest(DeleteCommand.class, true, false, false);
        illegalExecutionTest(DeleteCommand.class, false, false, false);
    }

    @Test
    public void javaCommandWithCorrectContentExecutesSuccessfully() throws Exception {
        JavaCommand javaCommand = new JavaCommand(){{
            setMonospaceFont(false);
            setPrivacy(CHAT);
            setId(CHAT_1);
            setDAO(dao);
            setArgument("Print");
        }};
        javaCommand.execute();

        // error because the command tries to execute Test in cache/CHAT/CHAT_1 folder which is empty
        // todo redirect cache folder for tests or something like that
        String expectedOutput = "Error: Could not find or load main class Print" + System.getProperty("line.separator");

        assertEquals(expectedOutput, javaCommand.getOutput());
    }

    @Test(expected = IllegalExecutionException.class)
    public void javaCommandWithEmptyArgumentThrowsException() {
        JavaCommand javaCommand = new JavaCommand(){{
            setPrivacy(CHAT);
            setId(CHAT_1);
            setDAO(dao);
            setArgument("");
        }};
        javaCommand.execute();
    }

    @Test
    public void javaCommandWithMissingContentThrowsException() {
        illegalExecutionTest(JavaCommand.class, true, true, false);
        illegalExecutionTest(JavaCommand.class, true, false, true);
        illegalExecutionTest(JavaCommand.class, false, true, true);
        illegalExecutionTest(JavaCommand.class, false, false, true);
        illegalExecutionTest(JavaCommand.class, false, true, false);
        illegalExecutionTest(JavaCommand.class, true, false, false);
        illegalExecutionTest(JavaCommand.class, false, false, false);
    }

    @Test
    public void javacCommandWithCorrectContentExecutesSuccessfully() {
        JavacCommand javacCommand = new JavacCommand(){{
            setMonospaceFont(false);
            setPrivacy(CHAT);
            setId(CHAT_1);
            setDAO(dao);
            setArgument("System.out.println(1);");
            wrapContentInMain("Test4");
        }};
        javacCommand.execute();

        String expectedOutput = "Successfully compiled!";

        assertEquals(expectedOutput, javacCommand.getOutput());
    }

    @Test
    public void javacCommandWithNoMainMethodContentExecutesSuccessfully() {
        JavacCommand javacCommand = new JavacCommand(){{
            setMonospaceFont(false);
            setPrivacy(CHAT);
            setId(CHAT_1);
            setDAO(dao);
            setArgument("");
            wrapContentInMain("Test4");
        }};
        javacCommand.execute();

        String expectedOutput = "Successfully compiled!";

        assertEquals(expectedOutput, javacCommand.getOutput());
    }

    @Test(expected = IllegalExecutionException.class)
    public void javacCommandWithEmptyArgumentThrowsException() {
        JavacCommand javacCommand = new JavacCommand(){{
            setPrivacy(CHAT);
            setId(CHAT_1);
            setDAO(dao);
            setArgument("");
        }};
        javacCommand.execute();
    }

    @Test(expected = NullPointerException.class)
    public void javacCommandWrapContentInMainMethodThrowsExceptionIfCalledWithNoContentSet() {
        JavacCommand javacCommand = new JavacCommand();
        javacCommand.wrapContentInMain("Test");
    }

    @Test
    public void javacCommandWithMissingContentThrowsException() {
        illegalExecutionTest(JavacCommand.class, true, true, false);
        illegalExecutionTest(JavacCommand.class, true, false, true);
        illegalExecutionTest(JavacCommand.class, false, true, true);
        illegalExecutionTest(JavacCommand.class, false, false, true);
        illegalExecutionTest(JavacCommand.class, false, true, false);
        illegalExecutionTest(JavacCommand.class, true, false, false);
        illegalExecutionTest(JavacCommand.class, false, false, false);
    }

    @Test
    public void noMonospaceParameterRemovesMarkdown() {
        String expectedOutput = "test";
        Command command = new Command() {
            @Override
            public void execute() {
                setOutput(expectedOutput);
            }
        };
        command.execute();
        command.setMonospaceFont(false);
        assertEquals(expectedOutput, command.getOutput());
    }

    @Test
    public void defaultOutputIsMonospace() {
        String output = "test";
        Command command = new Command() {
            @Override
            public void execute() {
                setOutput(output);
            }
        };
        command.execute();
        String expectedOutput = "```" + System.getProperty("line.separator") + output + System.getProperty("line.separator") + "```";
        assertEquals(expectedOutput, command.getOutput());
    }

    private void illegalExecutionTest(Class c, boolean daoIsSet, boolean privacyIsSet, boolean argumentIsSet) {
        try {
            BotDAO dao = null; Privacy privacy = null; Long id = null; String argument = null;
            if (daoIsSet)       dao      = this.dao;
            if (privacyIsSet) { privacy  = CHAT; id = CHAT_1; }
            if (argumentIsSet)  argument = "test";

            createCommand(c, dao, privacy, id, argument).execute();
            fail();
        } catch (IllegalExecutionException ignored) {
            // test passes if this block is executed
        }
    }

    private Command createCommand(Class c, BotDAO dao, Privacy privacy, Long id, String argument) {
        try {
            Command command = (Command) c.getConstructors()[0].newInstance();

            if (dao != null && command instanceof NeedsDAO)
                ((NeedsDAO) command).setDAO(dao);

            if (privacy != null && id != null  && command instanceof NeedsPrivacy)
                ((NeedsPrivacy) command).setPrivacy(privacy);

            if (argument != null && command instanceof NeedsArgument)
                ((NeedsArgument) command).setArgument(argument);

            return command;
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e); // better to e.printStackTrace(); ?
        }
    }
}

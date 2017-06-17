import bot.commands.*;
import bot.commands.interfaces.NeedsArgument;
import bot.commands.parameters.MainParameter;
import bot.commands.parameters.PrivacyParameter;
import bot.commands.visitors.Parameter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import parser.CommandParser;
import parser.ParserException;
import parser.UnknownCommandException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void noInputTest() {
        thrown.expect(ParserException.class);
        thrown.expectMessage("Reached end of input");
        test("", null);
    }

    @Test
    public void noInitCharTest() {
        thrown.expect(ParserException.class);
        thrown.expectMessage("Command must start with");
        test("asd", null);
    }

    @Test
    public void noParameterArgumentTest0() {
        thrown.expect(ParserException.class);
        thrown.expectMessage("Expected parameter argument. Reached end of input.");
        test("/javac -m", null);
    }

    @Test
    public void noArgumentTest0() {
        thrown.expect(ParserException.class);
        thrown.expectMessage("Expected argument for command. Reached end of input.");
        test("/javac -m Test", null);
    }

    @Test
    public void noArgumentTest1() {
        thrown.expect(ParserException.class);
        thrown.expectMessage("Expected argument for command. Reached end of input.");
        test("/javac", null);
    }

    @Test
    public void noArgumentTest2() {
        thrown.expect(ParserException.class);
        thrown.expectMessage("Expected argument for command. Reached end of input.");
        test("/delete", null);
    }

    @Test
    public void noParameterArgumentTest1() {
        thrown.expect(ParserException.class);
        thrown.expectMessage("Expected parameter argument. Got parameter ");
        test("/javac -m -p System.out.println(1);", null);
    }

    @Test
    public void javaTest0() {
        javaTest("Test", true); // /java -p Test
        javaTest("Test", false);// /java Test
    }

    @Test
    public void javaTest1() {
        javaTest("Sum 1 2 3", true);
        javaTest("Sum 1 2 3", false);
    }

    @Test
    public void javaTest2() {
        javaTest("Decide to_be not_to_be       1 2 3", true);
        javaTest("Decide to_be not_to_be       1 2 3", false);
    }

    @Test(expected = UnknownCommandException.class)
    public void javaSpecialCaseTest0() throws Exception {
        javaSpecialCaseTest("/Test");
    }

    @Test(expected = UnknownCommandException.class)
    public void javaSpecialCaseTest1() throws Exception {
        javaSpecialCaseTest("/Sum 1 2 3");
    }

    @Test(expected = UnknownCommandException.class)
    public void javaSpecialCaseTest2() throws Exception {
        javaSpecialCaseTest("/Decide to_be not_to_be    ");
    }

    @Test
    public void javacTest0() {
        JavacCommand javacCommand = new JavacCommand();
        javacCommand.setArgument("public class Test { public static void main(String[] args) { System.out.println(1); }}");
        test("/javac public class Test { public static void main(String[] args) { System.out.println(1); }}", javacCommand);
    }

    @Test
    public void javacTest1() {
        JavacCommand javacCommand = new JavacCommand();
        javacCommand.setArgument(
                "public class Test {" +
                        "   public static void main(String[] args) {\n" +
                        "        System.out.println(123);\n" +
                        "        for (int i = 0; i < 10; i++) {\n" +
                        "            i =  i + 1;\n" +
                        "            System.out.println(i);\n" +
                        "        }\n" +
                        "    }" +
                        "}"
        );
        test("/javac " +
                "public class Test {" +
                "   public static void main(String[] args) {\n" +
                "        System.out.println(123);\n" +
                "        for (int i = 0; i < 10; i++) {\n" +
                "            i =  i + 1;\n" +
                "            System.out.println(i);\n" +
                "        }\n" +
                "    }" +
                "}"
                , javacCommand);
    }

    @Test
    public void javacMainTest0() throws Exception {
        javacMainTest("/javac -m Test System.out.println(1);", "System.out.println(1);", "Test");
    }

    @Test
    public void javacMainTest1() throws Exception {
        String content = "int sum = 0; for (int i = 0; i < args.length; i++) sum += Integer.parseInt(args[i]); System.out.println(sum);";
        javacMainTest("/javac -m Sum " + content, content, "Sum");
    }

    @Test
    public void deleteTest() throws Exception {
        test("/delete Asd", c(DeleteCommand.class, "Asd"));
        test("/delete Asdasdasd asdasd", c(DeleteCommand.class, "Asdasdasd asdasd"));
    }

    @Test
    public void helpTest() throws Exception {
        test("/help", c(HelpCommand.class));
        test("/help asd", c(HelpCommand.class));
    }

    @Test
    public void listTest() throws Exception {
        test("/list", c(ListCommand.class));
        test("/list asd", c(ListCommand.class));
    }

    @Test
    public void listWithPrivacyTest() throws Exception {
        test("/list -p", c(ListCommand.class), new PrivacyParameter());
        test("/list -p asd asd", c(ListCommand.class), new PrivacyParameter());
    }

    @Test
    public void niceTest() throws Exception {
        test("/nice", c(NiceCommand.class));
        test("/nice asd", c(NiceCommand.class));
    }

    @Test
    public void upTest() throws Exception {
        test("/up", c(UpCommand.class));
        test("/up asd", c(UpCommand.class));
    }

    private static void javacMainTest(String input, String commandArgument, String parameterArgument) throws Exception {
        test(input, c(JavacCommand.class, commandArgument), p(MainParameter.class, parameterArgument));
    }

    private static void javaSpecialCaseTest(String input) throws Exception {
        test(input, c(JavaCommand.class, input.substring(1).trim()));
    }

    private static void javaTest(String argument, boolean isPrivate) {
        JavaCommand javaCommand = new JavaCommand();
        javaCommand.setArgument(argument);
        String input = "/java" + (isPrivate ? " -p " : " ") + argument;
        if (isPrivate) test(input, javaCommand, new PrivacyParameter());
        else test(input, javaCommand);
    }

    private static void test(String input, Command expectedCommand, Parameter... expectedParameters) {
        CommandParser parser = new CommandParser(input);
        parser.parse();

        Set<Parameter> expectedParametersSet = new HashSet<>();
        Collections.addAll(expectedParametersSet, expectedParameters);

        assertEquals(expectedCommand, parser.getCommand());
        assertEquals(expectedParametersSet, parser.getParameters());
    }

    private static NeedsArgument a(Class c, String arg) throws Exception {
        NeedsArgument argument = (NeedsArgument) c.getConstructors()[0].newInstance();
        argument.setArgument(arg);
        return argument;
    }

    private static Command c(Class c, String arg) throws Exception {
        return (Command) a(c, arg);
    }

    private static Command c(Class c) throws Exception {
        return (Command) c.getConstructors()[0].newInstance();
    }

    private static Parameter p(Class c, String arg) throws Exception {
        return (Parameter) a(c, arg);
    }
}

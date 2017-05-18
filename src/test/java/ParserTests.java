import parser.CommandParser;
import org.junit.Test;
import parser.ParserException;
import parser.Token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created on 18.05.2017.
 */
public class ParserTests {

    @Test(expected = ParserException.class, timeout = 500)
    public void exceptionIfInvalidCommandTest() {
        test("/unknown", null);
    }

    @Test(expected = ParserException.class, timeout = 500)
    public void exceptionIfUnknownCommandTest() {
        test("java", null);
    }

    @Test(expected = ParserException.class, timeout = 500)
    public void exceptionIfNoParamArgumentTest() {
        test("/java -m", null);
    }

    @Test(expected = ParserException.class, timeout = 500)
    public void exceptionIfUnknownParamTest() {
        test("/java -s", null);
    }

    @Test(timeout = 500)
    public void testValidJava() {
        test("/java -p Shit",
                new Token("/java", "Shit"),
                new Token("-p")
        );

        test("/java Shit",
                new Token("/java", "Shit")
        );
    }

    @Test(timeout = 500)
    public void testValidJavac() {
        test("/javac -p -m Shit System.out.println(\"asd\")",
                new Token("/javac", "System.out.println(\"asd\")"),
                new Token("-p"),
                new Token("-m", "Shit")
        );
    }

    private void test(String input, Token command, Token... parameters) {
        CommandParser parser = new CommandParser(input);
        parser.parse();

        assertEquals(command.getArgument(), parser.getCommand().getArgument());
        assertEquals(command.getValue(), parser.getCommand().getValue());

        if (parameters.length == 0) assertTrue(parser.getParameters().isEmpty());

        for (Token param : parameters) {
            boolean ok = false;
            for (Token parserParam : parser.getParameters()) {
                // Av == Bv && ((Aa == null && Ba == null) || Aa == Ba)
                if (param.getValue().equals(parserParam.getValue()) && ((
                                param.getArgument() == null && parserParam.getArgument() == null) ||
                                param.getArgument().equals(parserParam.getArgument()))) {
                    ok = true;
                    break;
                }
            }
            assertTrue(ok);
        }
    }
}

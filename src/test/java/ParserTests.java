import parser.CommandParser;
import org.junit.Test;
import parser.ParserException;
import parser.Token;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created on 18.05.2017.
 */
public class ParserTests {

    @Test(expected = ParserException.class, timeout = 500)
    public void invalidCommandTest() {
        test("/unknown", null);
    }

    @Test(expected = ParserException.class, timeout = 500)
    public void unknownCommandTest() {
        test("java", null);
    }

    @Test(expected = ParserException.class, timeout = 500)
    public void noParamArgumentTest() {
        test("/java -m", null);
    }

    @Test(expected = ParserException.class, timeout = 500)
    public void unknownParamTest() {
        test("/java -s", null);
    }

    @Test(expected = ParserException.class, timeout = 500)
    public void unknownParamTest2() {
        test("/java -s Shit", null);
    }

    @Test(timeout = 500)
    public void validJavaTest() {
        test("/java -p Shit",
                Token.command("/java", "Shit"),
                Token.parameter("-p")
        );

        test("/java Shit",
                Token.command("/java", "Shit")
        );

        test("/java Decide A B C D",
                Token.command("/java", "Decide A B C D")
        );
    }

    @Test(timeout = 500)
    public void validJavacTest() {
        test("/javac -p -m Shit System.out.println(\"asd\")",
                Token.command("/javac", "System.out.println(\"asd\")"),
                Token.parameter("-p"),
                Token.parameter("-m", "Shit")
        );

        test("/javac -m Shit -p System.out.println(\"asd\")",
                Token.command("/javac", "System.out.println(\"asd\")"),
                Token.parameter("-p"),
                Token.parameter("-m", "Shit")
        );

        test("/javac -m Shit System.out.println(\"asd\")",
                Token.command("/javac", "System.out.println(\"asd\")"),
                Token.parameter("-m", "Shit")
        );

        test("/javac -p public class Shit { public static void main(String[] args) { System.out.println(\"asd\") } }",
                Token.command("/javac", "public class Shit { public static void main(String[] args) { System.out.println(\"asd\") } }"),
                Token.parameter("-p")
        );

        test("/javac public class Shit { public static void main(String[] args) { System.out.println(\"asd\") } }",
                Token.parameter("/javac", "public class Shit { public static void main(String[] args) { System.out.println(\"asd\") } }")
        );
    }

    @Test(timeout = 500)
    public void validDeleteTest() {
        test("/delete -p Shit",
                Token.command("/delete", "Shit"),
                Token.parameter("-p")
        );

        test("/delete Shit",
                Token.command("/delete", "Shit")
        );
    }


    @Test(timeout = 500)
    public void ValidListTest() {
        test("/list -p",
                Token.command("/list"),
                Token.parameter("-p")
        );

        test("/list",
                Token.command("/list")
        );
    }

    @Test(timeout = 500)
    public void validSimpleTest() {
        test("/up",
                Token.command("/up")
        );

        test("/help",
                Token.command("/help")
        );

        test("/nice",
                Token.command("/nice")
        );
    }

    @Test(timeout = 500)
    public void validWrongsTest() {
        test("/java Shit -s",
                Token.command("/java", "Shit -s")
        );

        test("/list Shit -s",
                Token.command("/list")
        );

        test("/delete this is whatever",
                Token.command("/delete", "this is whatever")
        );
    }

    private void test(String input, Token command, Token... parameters) {
        CommandParser parser = new CommandParser(input);
        parser.parse();

        assertEquals(command.getValue(), parser.getCommand().getValue());
        assertEquals(command.getArgument(), parser.getCommand().getArgument());

        Map<String, Token.ParameterToken> parserParameters = parser.getParameters();

        assertEquals(parameters.length, parserParameters.size());

        for (Token param : parameters) {
            if (parserParameters.containsKey(param.getValue())) {
                if (param.getArgument() == null) assertTrue(parserParameters.get(param.getValue()).getArgument() == null);
                else assertEquals(param.getArgument(), parserParameters.get(param.getValue()).getArgument());
            } else {
                fail();
            }
        }
    }
}

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
                new Token("/java", "Shit"),
                new Token("-p")
        );

        test("/java Shit",
                new Token("/java", "Shit")
        );
    }

    @Test(timeout = 500)
    public void validJavacTest() {
        test("/javac -p -m Shit System.out.println(\"asd\")",
                new Token("/javac", "System.out.println(\"asd\")"),
                new Token("-p"),
                new Token("-m", "Shit")
        );

        test("/javac -m Shit -p System.out.println(\"asd\")",
                new Token("/javac", "System.out.println(\"asd\")"),
                new Token("-p"),
                new Token("-m", "Shit")
        );

        test("/javac -m Shit System.out.println(\"asd\")",
                new Token("/javac", "System.out.println(\"asd\")"),
                new Token("-m", "Shit")
        );

        test("/javac -p public class Shit { public static void main(String[] args) { System.out.println(\"asd\") } }",
                new Token("/javac", "public class Shit { public static void main(String[] args) { System.out.println(\"asd\") } }"),
                new Token("-p")
        );

        test("/javac public class Shit { public static void main(String[] args) { System.out.println(\"asd\") } }",
                new Token("/javac", "public class Shit { public static void main(String[] args) { System.out.println(\"asd\") } }")
        );
    }

    @Test(timeout = 500)
    public void validDeleteTest() {
        test("/delete -p Shit",
                new Token("/delete", "Shit"),
                new Token("-p")
        );

        test("/delete Shit",
                new Token("/delete", "Shit")
        );
    }


    @Test(timeout = 500)
    public void ValidListTest() {
        test("/list -p",
                new Token("/list"),
                new Token("-p")
        );

        test("/list",
                new Token("/list")
        );
    }

    @Test(timeout = 500)
    public void validSimpleTest() {
        test("/up",
                new Token("/up")
        );

        test("/help",
                new Token("/help")
        );

        test("/nice",
                new Token("/nice")
        );
    }

    @Test(timeout = 500)
    public void validWrongsTest() {
        test("/java Shit -s",
                new Token("/java", "Shit -s")
        );

        test("/list Shit -s",
                new Token("/list")
        );

        test("/delete this is whatever",
                new Token("/delete", "this is whatever")
        );
    }

    private void test(String input, Token command, Token... parameters) {
        CommandParser parser = new CommandParser(input);
        parser.parse();
        
        assertEquals(command.getValue(), parser.getCommand().getValue());
        assertEquals(command.getArgument(), parser.getCommand().getArgument());

        assertEquals(parameters.length, parser.getParameters().size());

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

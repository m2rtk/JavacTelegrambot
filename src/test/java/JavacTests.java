import javac.Code;
import javac.Compiled;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavacTests {
    @Test
    public void compileTest1() throws Exception {
        compileTest("Test");
    }

    @Test
    public void compileTest2() throws Exception {
        compileTest("HelloWorld");
    }

    @Test
    public void compileTest3() throws Exception {
        compileTest("M8");
    }

    @Test
    public void runTest1() throws Exception {
        runTest("HelloWorld", new String[0], "Hello World!");
    }

    @Test
    public void runTest2() throws Exception {
        runTest("Test", new String[]{"wow"}, "wow");
    }

    @Test
    public void runTest3() throws Exception {
        runTest("Sum", new String[]{"1", "2"}, "3");
    }

    private void compileTest(String name) throws Exception {
        Code code = new Code(Utils.readSource(name), null, null);
        String path = getClass().getClassLoader().getResource("src/").getPath();
        Utils.setObjectField(code, "classPath", path);
        assertTrue(code.compile());
        assertTrue(Arrays.equals(code.getCompiled().getByteCode(), Utils.readOut(name)));
    }

    private void runTest(String name, String[] args, String expectedOut) throws Exception {
        Compiled compiled = new Compiled(Utils.readOut(name), name);
        String path = getClass().getClassLoader().getResource("out/").getPath();
        Utils.setObjectField(compiled, "classPath", path);
        compiled.run(args);
        assertEquals(expectedOut, compiled.getOut().trim());
    }
}

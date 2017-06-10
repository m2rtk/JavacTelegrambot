import javac.ClassFile;
import javac.Compiler;
import javac.Executor;
import javac.JavaFile;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavacTests {

    @Test
    public void helloWorldCompilesCorrectly() throws Exception {
        successfulCompilationTest("HelloWorld");
    }

    @Test
    public void printCompilesCorrectly() throws Exception {
        successfulCompilationTest("Print");
    }

    @Test
    public void sumCompilesCorrectly() throws Exception {
        successfulCompilationTest("Sum");
    }

    @Test
    public void m8CompilesCorrectly() throws Exception {
        successfulCompilationTest("M8");
    }

    @Test
    public void incorrectJavaFileDoesNotCompileAndOutputsErrorMessage() throws Exception {
        JavaFile brokenJavaFile = new JavaFile(
                        "public class Broken {\n" +
                        "   public static void main(String[] args) {\n" +
                        "       System.out.println(1)\n" + // missing semicolon
                        "   }\n" +
                        "}"
        );

        Compiler compiler = new Compiler(brokenJavaFile);

        assertTrue(!compiler.compile());
        assertTrue(!compiler.getOutputMessage().isEmpty()); // failed compilation should output info about the error
        assertTrue(compiler.getOutputMessage().contains("error: ';' expected"));
    }

    @Test
    public void printClassExecutesCorrectly() throws Exception {
        ClassFile print = Utils.readClassFile("Print");
        Executor executor = new Executor(print);
        String argument = "Test";
        String expected = argument + System.getProperty("line.separator");
        executor.run(argument);
        assertEquals(expected, executor.getOutputMessage());
    }

    @Test
    public void sumClassExecutesCorrectly() throws Exception {
        ClassFile sum = Utils.readClassFile("Sum");
        Executor executor = new Executor(sum);
        executor.run(String.valueOf(1), String.valueOf(2));
        String expected = String.valueOf(1 + 2) + System.getProperty("line.separator");
        assertEquals(expected, executor.getOutputMessage());
    }

    @Test
    public void helloWorldClassExecutesCorrectly() throws Exception {
        ClassFile helloWorld = Utils.readClassFile("HelloWorld");
        Executor executor = new Executor(helloWorld);
        executor.run();
        String expected = "Hello World!" + System.getProperty("line.separator");
        assertEquals(expected, executor.getOutputMessage());
    }

    @Test
    public void tooTimeConsumingExecutionTimesOut() throws Exception {
        ClassFile timeout = Utils.readClassFile("Timeout");
        Executor executor = new Executor(timeout);
        executor.run();
        String expected = "Timed out after [0-9]+ milliseconds.";
        assertTrue(executor.getOutputMessage().matches(expected));
    }

    @Test
    public void javaFileExtractsSimpleNameCorrectly() {
        nameExtractTest("public class Test {}", "Test");
        nameExtractTest("class Test {}", "Test");
        nameExtractTest("public class asdqefjweklfjwlf {}", "asdqefjweklfjwlf");
        nameExtractTest("class Asdqefjweklfjwlf {}", "Asdqefjweklfjwlf");
        nameExtractTest("public class a1a1a1 {}", "a1a1a1");
        nameExtractTest("class a1a1a1 {}", "a1a1a1");
    }

    @Test
    public void javaFileExtractsNameWithCommentsCorrectly() {
        nameExtractTest("//public class WrongName {}\n public class RightName {}", "RightName");
        nameExtractTest("//asdljqiodjoj class\n public class RightName {} //asdasd", "RightName");
        nameExtractTest("//class Wrong\n  class /*class Wronger{}*/ Test /*class Wrongest {}*/ {}", "Test");
    }

    @Test
    public void javaFileExtractNameWithImportsAndCommentsCorrectly() {
        nameExtractTest("import javac.ClassFile; \n" +
                        "import javac.Compiler; \n" +
                        "//class Wrong {} \n" +
                        "class Test {}",
                "Test"
        );
    }

    @Test
    public void javaFileExtractsFirstNameWithMultipleClassesCorrectly() {
        nameExtractTest("class Test {} \n class Wrong {}", "Test");
    }

    @Test
    public void javaFileExtractsNoNameIfInvalidInput() {
        nameExtractTest("clas Test {}", "FailedToParseClassName");
        nameExtractTest("Test {}", "FailedToParseClassName");
        nameExtractTest("Test", "FailedToParseClassName");
        nameExtractTest("class Test { public static void main(String[] args) { System.out.println(1) }}", "FailedToParseClassName");
    }

    private void nameExtractTest(String source, String expectedName) {
        JavaFile javaFile = new JavaFile(source);
        assertEquals(expectedName, javaFile.getClassName());
    }

    private void successfulCompilationTest(String className) throws Exception {
        JavaFile javaFile = Utils.readJavaFile(className);
        Compiler compiler = new Compiler(javaFile);

        assertTrue(compiler.compile());
        assertTrue(compiler.getOutputMessage().isEmpty()); // successful compilation outputs no message

        ClassFile expected = Utils.readClassFile(className);
        assertEquals(expected, compiler.getOutputClass());
    }
}

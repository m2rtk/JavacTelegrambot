import com.github.m2rtk.telegram.dao.BotDAO;
import com.github.m2rtk.telegram.dao.Privacy;
import com.github.m2rtk.telegram.dao.WriteToDiskBotDAO;
import com.github.m2rtk.telegram.javac.ClassFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.github.m2rtk.telegram.dao.Privacy.CHAT;
import static com.github.m2rtk.telegram.dao.Privacy.USER;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class WriteToDiskBotDAOTests {
    private static final BotDAO dao = new WriteToDiskBotDAO();
    private static final Long CHAT_1 = -1L;
    private static final Long CHAT_2 = -2L;
    private static final Long USER_1 =  1L;
    private static final Long USER_2 =  2L;

    private ClassFile print, sum, helloWorld;
    private Map<String, ClassFile> testFiles;

    @Before
    public void init() throws Exception {
        testFiles = new HashMap<>();
        print      = Utils.readClassFile("Print");
        sum        = Utils.readClassFile("Sum");
        helloWorld = Utils.readClassFile("HelloWorld");
        testFiles.put("Print", print);
        testFiles.put("Sum", sum);
        testFiles.put("HelloWorld", helloWorld);
    }

    @After
    public void after() {
        clearTestFilesFromCache();
    }

    @Test
    public void addTest1() {
        addTest(print, USER_1, USER);
    }

    @Test
    public void addTest2() {
        addTest(print, USER_2, USER);
    }

    @Test
    public void addTest3() {
        addTest(sum, USER_1, USER);
    }

    @Test
    public void addTest4() {
        addTest(sum, CHAT_1, CHAT);
    }

    @Test
    public void addTest5() {
        addTest(helloWorld, CHAT_1, CHAT);
    }

    @Test
    public void removeSuccessTest1() {
        createCacheFile("Print", USER_1, USER);
        removeTest("Print", USER_1, USER, true);
    }

    @Test
    public void removeSuccessTest2() {
        createCacheFile("Print", USER_2, USER);
        removeTest("Print", USER_2, USER, true);
    }

    @Test
    public void removeSuccessTest3() {
        createCacheFile("Sum", CHAT_1, CHAT);
        removeTest("Sum", CHAT_1, CHAT, true);
    }

    @Test
    public void removeSuccessTest4() {
        createCacheFile("HelloWorld", CHAT_2, CHAT);
        removeTest("HelloWorld", CHAT_2, CHAT, true);
    }

    @Test
    public void removeFailTest1() {
        removeTest("Print", USER_1, USER, false);
    }

    @Test
    public void removeFailTest2() {
        removeTest("Dsa", CHAT_2, CHAT, false);
    }

    @Test
    public void removeFailTest3() {
        removeTest("Banana", USER_2, USER, false);
    }

    @Test
    public void removeFailTest4() {
        removeTest("Sum", CHAT_1, CHAT, false);
    }

    @Test
    public void removeFailTest5() {
        createCacheFile("HelloWorld", CHAT_1, CHAT);
        removeTest("HelloWorld", CHAT_2, CHAT, false);
    }

    @Test
    public void getSuccessTest() {
        createCacheFile("Print", CHAT_1, CHAT);
        getTest("Print", CHAT_1, CHAT, true);
    }

    @Test
    public void getFailTest() {
        getTest("Fail", CHAT_1, CHAT, false);
    }

    @Test
    public void getMiscTest() {
        createCacheFile("Print", CHAT_1, CHAT);
        createCacheFile("Print", USER_1, USER);
        createCacheFile("Sum",  USER_2, USER);

        getTest("HelloWorld", CHAT_2, CHAT, false);
        getTest("Print", CHAT_2, CHAT, false);
        getTest("Print", USER_2, USER, false);
        getTest("Sum", CHAT_2, CHAT, false);
        getTest("Sum", USER_2, USER, true);
        getTest("Sum", USER_1, USER, false);
    }

    @Test
    public void getAllTest1() {
        createCacheFile("Print", CHAT_1, CHAT);
        getAllTest(CHAT_1, CHAT, new ClassFile[]{testFiles.get("Print")});
    }

    @Test
    public void getAllTest2() {
        createCacheFile("Print", CHAT_1, CHAT);
        createCacheFile("Sum",  CHAT_1, CHAT);
        createCacheFile("HelloWorld",  CHAT_2, CHAT);
        getAllTest(CHAT_1, CHAT, new ClassFile[]{testFiles.get("Print"), testFiles.get("Sum")});
        getAllTest(CHAT_2, CHAT, new ClassFile[]{testFiles.get("HelloWorld")});
    }

    @Test
    public void getAllTest3() {
        createCacheFile("HelloWorld", USER_1, USER);
        createCacheFile("HelloWorld", USER_2, USER);
        createCacheFile("Sum",  USER_1, USER);
        getAllTest(USER_1, USER, new ClassFile[]{testFiles.get("HelloWorld"), testFiles.get("Sum")});
        getAllTest(USER_2, USER, new ClassFile[]{testFiles.get("HelloWorld")});
    }

    @Test
    public void getAllTest4() {
        createCacheFile("HelloWorld", USER_1, USER);
        createCacheFile("HelloWorld", USER_2, USER);
        createCacheFile("Sum",  USER_1, USER);
        createCacheFile("Sum",  USER_2, USER);
        createCacheFile("Print",  USER_1, USER);
        createCacheFile("Print",  USER_2, USER);
        createCacheFile("HelloWorld", CHAT_1, CHAT);
        createCacheFile("HelloWorld", CHAT_2, CHAT);
        createCacheFile("Sum",  CHAT_1, CHAT);
        createCacheFile("Sum",  CHAT_2, CHAT);
        createCacheFile("Print",  CHAT_1, CHAT);
        createCacheFile("Print",  CHAT_2, CHAT);
        getAllTest(USER_1, USER, new ClassFile[]{testFiles.get("HelloWorld"), testFiles.get("Sum"), testFiles.get("Print")});
        getAllTest(USER_2, USER, new ClassFile[]{testFiles.get("HelloWorld"), testFiles.get("Sum"), testFiles.get("Print")});
        getAllTest(CHAT_1, CHAT, new ClassFile[]{testFiles.get("HelloWorld"), testFiles.get("Sum"), testFiles.get("Print")});
        getAllTest(CHAT_2, CHAT, new ClassFile[]{testFiles.get("HelloWorld"), testFiles.get("Sum"), testFiles.get("Print")});
    }

    @Test
    public void getAllNoFilesTest() {
        getAllTest(USER_1, USER, new ClassFile[0]);
        getAllTest(USER_2, USER, new ClassFile[0]);
        getAllTest(CHAT_1, CHAT, new ClassFile[0]);
        getAllTest(CHAT_2, CHAT, new ClassFile[0]);
    }

    @Test
    public void isEmptyTest1() {
        isEmptyTest(USER_1, USER, true);
        isEmptyTest(USER_2, USER, true);
        isEmptyTest(CHAT_1, CHAT, true);
        isEmptyTest(CHAT_2, CHAT, true);
    }

    @Test
    public void isEmptyTest2() {
        createCacheFile("Print", USER_1, USER);
        createCacheFile("Print", CHAT_1, CHAT);
        isEmptyTest(USER_1, USER, false);
        isEmptyTest(USER_2, USER, true);
        isEmptyTest(CHAT_1, CHAT, false);
        isEmptyTest(CHAT_2, CHAT, true);
    }

    @Test
    public void containsTest1() {
        containsTest("Print", USER_1, USER, false);
        containsTest("Print", USER_2, USER, false);
        containsTest("Print", CHAT_1, CHAT, false);
        containsTest("Print", CHAT_2, CHAT, false);
    }

    @Test
    public void containsTest2() {
        createCacheFile("Print", USER_1, USER);
        createCacheFile("Print", CHAT_1, CHAT);
        containsTest("Print", USER_1, USER, true);
        containsTest("Print", USER_2, USER, false);
        containsTest("Print", CHAT_1, CHAT, true);
        containsTest("Print", CHAT_2, CHAT, false);
    }

    private void containsTest(String name, Long id, Privacy privacy, boolean expected) {
        assertEquals(expected, dao.contains(name, id, privacy));
    }

    private void isEmptyTest(Long id, Privacy privacy, boolean expected) {
        assertEquals(expected, dao.isEmpty(id, privacy));
    }

    private void getAllTest(Long id, Privacy privacy, ClassFile[] expected) {
        Set<ClassFile> classFiles = dao.getAll(id, privacy);
        assertEquals("getAll set and expected set are not equal in length.",expected.length, classFiles.size());
        int c = 0;
        for (ClassFile c1 : classFiles) {
            for (ClassFile c2 : expected) {
                if (Arrays.equals(c1.getByteCode(), c2.getByteCode())) c++;
            }
        }
        assertTrue("getAll set and expected set are not equal.", c == expected.length);
    }

    private void getTest(String name, Long id, Privacy privacy, boolean expected) {
        ClassFile compiled = dao.get(name, id, privacy);
        assertEquals(expected, compiled != null);
        if (expected) {
            assertEquals("Get returned invalid name.", compiled.getClassName(), name);
            assertTrue("Get returned invalid bytecode.", Arrays.equals(compiled.getByteCode(), testFiles.get(name).getByteCode()));
        }
    }

    private void addTest(ClassFile compiled, Long id, Privacy privacy) {
        dao.add(compiled, id, privacy);
        Path path = Paths.get("cache/" + privacy + "/" + id);
        assertTrue("File doesn't exist after add", Files.exists(path));
        try {
            if (Files.exists(path.resolve(compiled.getClassName() + ".class")))
                Files.delete(path.resolve(compiled.getClassName() + ".class"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeTest(String name, Long id, Privacy privacy, boolean expected) {
        assertEquals(expected, dao.remove(name, id, privacy));
        assertTrue("File still exists after remove", !Files.exists(Paths.get("cache/" + privacy + "/" + id + "/" + name + ".class")));
    }

    private void createCacheFile(String name, Long id, Privacy privacy) {
        try {
            Path path = Paths.get("cache/" + privacy + "/" + id);
            Files.createDirectories(path);
            Files.write(path.resolve(name + ".class"), testFiles.get(name).getByteCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearTestFilesFromCache() {
        try {
            Path path;
            for (int i = 1; i < 3; i++) {
                path = Paths.get("cache/" + USER + "/" + i);
                for (String file : testFiles.keySet()) {
                    Files.deleteIfExists(path.resolve(file + ".class"));
                }
                Files.deleteIfExists(path);
            }

            for (int i = -2; i < 0; i++) {
                path = Paths.get("cache/" + CHAT + "/" + i);
                for (String file : testFiles.keySet()) {
                    Files.deleteIfExists(path.resolve(file + ".class"));
                }
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

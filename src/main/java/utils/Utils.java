package utils;

import dao.Privacy;
import javac.ClassFile;
import javac.JavaFile;
import org.telegram.telegrambots.api.objects.Update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static dao.Privacy.CHAT;

public class Utils {

    private Utils() {}

    /**
     * Surrounds input parameter with ``` and line.separator's
     * @param text input text.
     * @return surrounded text.
     */
    public static String toMonospace(String text) {
        return  "```" + System.getProperty("line.separator") + text + System.getProperty("line.separator") + "```";
    }

    /**
     * Gets id corresponding to privacy from update.
     * @param privacy privacy. CHAT or USER.
     * @param update  update to get id from.
     * @return chatId if CHAT, userId if USER
     */
    public static Long getId(Privacy privacy, Update update) {
        return privacy == CHAT ? update.getMessage().getChatId() : update.getMessage().getFrom().getId();
    }

    /**
     * Writes ClassFile to disk named as: classFile.getClassName() + ".class"
     * @param classFile classfile to write.
     * @return Path of written file.
     */
    public static Path write(ClassFile classFile) {
        if (classFile == null) return null;
        try {
            return Files.write(Paths.get(classFile.getClassName() + ".class"), classFile.getByteCode());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Writes JavaFile to disk named as: javaFile.getClassName() + ".java"
     * @param javaFile javafile to write.
     * @return Path of written file.
     */
    public static Path write(JavaFile javaFile) {
        if (javaFile == null) return null;
        try {
            return Files.write(Paths.get(javaFile.getClassName()  + ".java"), javaFile.getSource().getBytes());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Deletes JavaFile from disk.
     * @param javaFile javaFile to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public static boolean delete(JavaFile javaFile) {
        if (javaFile == null) return false;
        try {
            Files.delete(Paths.get(javaFile.getClassName() + ".java"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Deletes ClassFile from disk.
     * @param classFile classFile to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public static boolean delete(ClassFile classFile) {
        if (classFile == null) return false;
        try {
            Files.delete(Paths.get(classFile.getClassName() + ".class"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Reads the ClassFile corresponding to JavaFile from disk.
     * @param javaFile javaFile of which classFile to read.
     * @return ClassFile if found, null otherwise.
     */
    public static ClassFile readClassFile(JavaFile javaFile) {
        try {
            byte[] byteCode = Files.readAllBytes(Paths.get(javaFile.getClassName() + ".class"));
            return new ClassFile(javaFile.getClassName(), byteCode);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Gets all lines from InputStream as String
     * @param is InputStream
     * @return String of all lines. Lines separated by System.getProperty("line.separator").
     *         Empty String if exception occurred while reading from InputStream.
     */
    public static String getLines(InputStream is) {
        try {
            String line;
            StringBuilder sb = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1));

            while ((line = in.readLine()) != null) sb.append(line).append(System.getProperty("line.separator"));

            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Creates an array which contains java command with parameters
     * @param classFile ClassFile, getClassName() is used.
     * @param classPath classPath as String
     * @param args arguments as String
     * @return Array of Strings, example: ["java", "-classpath", "/path/to/files", "Print", "Hello", "World!"]
     */
    public static String[] createJavaCommand(ClassFile classFile, String classPath, String[] args) {
        String[] completeArgs;
        if (classPath != null) {
            completeArgs = new String[args.length + 4];
            completeArgs[0] = "java";
            completeArgs[1] = "-classpath";
            completeArgs[2] = classPath;
            completeArgs[3] = classFile.getClassName();
            System.arraycopy(args, 0, completeArgs, 4, args.length);
        } else { // mainly for testing
            completeArgs = new String[args.length + 2];
            completeArgs[0] = "java";
            completeArgs[1] = classFile.getClassName();
            System.arraycopy(args, 0, completeArgs, 2, args.length);
        }
        return completeArgs;
    }
}

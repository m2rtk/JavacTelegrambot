package javac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Utils {

    private Utils() {} // prevent instantiation

    static Path write(ClassFile classFile) {
        if (classFile == null) return null;
        try {
            return Files.write(Paths.get(classFile.getClassName() + ".class"), classFile.getByteCode());
        } catch (IOException e) {
            return null;
        }
    }


    static Path write(JavaFile javaFile) {
        if (javaFile == null) return null;
        try {
            return Files.write(Paths.get(javaFile.getClassName()  + ".java"), javaFile.getSource().getBytes());
        } catch (IOException e) {
            return null;
        }
    }

    static boolean delete(JavaFile javaFile) {
        if (javaFile == null) return false;
        try {
            Files.delete(Paths.get(javaFile.getClassName() + ".java"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    static boolean delete(ClassFile classFile) {
        if (classFile == null) return false;
        try {
            Files.delete(Paths.get(classFile.getClassName() + ".class"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    static ClassFile readClassFile(JavaFile javaFile) {
        try {
            byte[] byteCode = Files.readAllBytes(Paths.get(javaFile.getClassName() + ".class"));
            return new ClassFile(javaFile.getClassName(), byteCode);
        } catch (IOException e) {
            return null;
        }
    }

    static String getLines(InputStream is) {
        try {
            String line;
            StringBuilder sb = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1));

            while ((line = in.readLine()) != null)
                sb.append(line).append(System.getProperty("line.separator"));

            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }
}

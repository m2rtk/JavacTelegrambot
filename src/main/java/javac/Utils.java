package javac;

import dao.Privacy;

import java.io.*;
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
        String line;
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(is)); //todo add charset

        try {
            while ((line = in.readLine()) != null) sb.append(line).append(System.getProperty("line.separator"));
        } catch (IOException e) {
            return "";
        }

        return sb.toString();
    }
}

package javac;

import dao.Privacy;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class Utils {

    private Utils() {} // prevent instantiation

    static void write(ClassFile classFile) throws IOException {
        Files.write(Paths.get(classFile.getClassName() + ".class"), classFile.getByteCode());
    }

    static void write(JavaFile javaFile) throws IOException {
        Files.write(Paths.get(javaFile.getClassName()  + ".java"), javaFile.getSource().getBytes());
    }

    static void delete(JavaFile javaFile) throws IOException {
        Files.delete(Paths.get(javaFile.getClassName() + ".java"));
    }

    static void delete(ClassFile classFile) throws IOException {
        Files.delete(Paths.get(classFile.getClassName() + ".class"));
    }

    static ClassFile readClassFile(JavaFile javaFile) {
        try {
            byte[] byteCode = Files.readAllBytes(Paths.get(javaFile.getClassName() + ".class"));
            return new ClassFile(javaFile.getClassName(), byteCode);
        } catch (IOException e) {
            return null;
        }
    }

    static void write(ClassFile classFile, Privacy privacy, Long id) {

    }

    static String getLines(InputStream is) throws IOException {
        String line;
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(is)); //todo add charset
        while ((line = in.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }
}

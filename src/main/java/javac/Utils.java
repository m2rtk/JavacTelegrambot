package javac;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {

    // TODO: 04.04.2017 Delete all the oneliner methods
    static void writeSmallBinaryFile(byte[] aBytes, String aFileName) throws IOException {
        Files.write(Paths.get(aFileName), aBytes); //creates, overwrites
    }

    static byte[] readSmallBinaryFile(String aFileName) throws IOException {
        return Files.readAllBytes(Paths.get(aFileName));
    }

    static boolean exists(String filename) {
        return Files.exists(Paths.get(filename));
    }

    static void delete(String filename) throws IOException {
        Files.delete(Paths.get(filename));
    }

    static void writeFile(String content, String filename) {
        BufferedWriter bw = null;
        OutputStreamWriter fw = null;

        try {
           // fw = new FileWriter(filename);
            fw = new OutputStreamWriter(new FileOutputStream(filename), "utf-8");
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static String getLines(InputStream is) throws IOException {
        String line;
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        while ((line = in.readLine()) != null) {
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }
}

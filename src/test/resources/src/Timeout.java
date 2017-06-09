
public class Timeout {
    public static void main(String[] args) throws Exception {
        for (int i = 1; i < 101; i++) {
            System.out.println(i);
            Thread.sleep(100);
        }
    }
}
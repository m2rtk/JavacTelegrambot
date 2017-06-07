package javac;

public class ClassFile {
    private String className;
    private byte[] byteCode;

    public ClassFile(String className, byte[] byteCode) {
        this.className = className;
        this.byteCode = byteCode;
    }

    public String getClassName() {
        return className;
    }

    public byte[] getByteCode() {
        return byteCode;
    }
}

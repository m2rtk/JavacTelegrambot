package com.github.m2rtk.telegram.javac;

import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassFile classFile = (ClassFile) o;

        return className.equals(classFile.className) && Arrays.equals(byteCode, classFile.byteCode);
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + Arrays.hashCode(byteCode);
        return result;
    }
}

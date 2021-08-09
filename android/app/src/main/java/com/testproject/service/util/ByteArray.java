package com.testproject.service.util;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteArray {
    private final int INIT_CAPACITY = 2048;
    private byte[] buffer;
    private int size;

    public ByteArray() {
        buffer = new byte[INIT_CAPACITY];
    }

    public int size() {
        return size;
    }

    private void grow() {
        buffer = Arrays.copyOf(buffer, buffer.length * 2);
    }

    public void append(byte[] buf) {
        while (buffer.length < size + buf.length) {
            grow();
        }
        System.arraycopy(buf, 0, buffer, size, buf.length);
        size += buf.length;
    }

    public ByteBuffer toByteBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        byteBuffer.put(buffer);
        return byteBuffer;
    }

    public byte[] array() {
        return Arrays.copyOf(buffer, size);
    }
}

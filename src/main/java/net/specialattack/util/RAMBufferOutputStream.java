package net.specialattack.util;

import java.io.IOException;
import java.io.OutputStream;

public class RAMBufferOutputStream extends OutputStream {

    private RAMBuffer buffer;
    private byte[] tinyArray = new byte[1];

    public RAMBufferOutputStream(RAMBuffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("Buffer must not be null");
        }
        this.buffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {
        this.tinyArray[0] = (byte) b;
        this.buffer.write(this.tinyArray);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.buffer.write(b, off, len);
    }
}

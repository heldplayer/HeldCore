package net.specialattack.util;

import java.io.IOException;
import java.io.InputStream;

public class RAMBufferInputStream extends InputStream {

    private RAMBuffer buffer;
    private byte[] tinyArray = new byte[1];
    private int markedPosition = -1;
    private int readLimit = -1;

    public RAMBufferInputStream(RAMBuffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("Buffer must not be null");
        }
        this.buffer = buffer;
    }

    @Override
    public int read() throws IOException {
        if (this.readLimit < 0) {
            this.markedPosition = -1;
        } else {
            this.readLimit--;
            if (this.readLimit < 0) {
                this.markedPosition = -1;
            }
        }
        this.buffer.read(this.tinyArray);
        return this.tinyArray[0] & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.readLimit < 0) {
            this.markedPosition = -1;
        } else {
            this.readLimit -= len;
            if (this.readLimit < 0) {
                this.markedPosition = -1;
            }
        }
        this.buffer.read(b, off, len);
        return len;
    }

    @Override
    public long skip(long n) throws IOException {
        if (this.readLimit < 0) {
            this.markedPosition = -1;
        } else {
            this.readLimit -= n;
            if (this.readLimit < 0) {
                this.markedPosition = -1;
            }
        }
        this.buffer.skip((int) n);
        return n;
    }

    @Override
    public int available() throws IOException {
        return this.buffer.getAvailable();
    }

    @Override
    public synchronized void mark(int readlimit) {
        if (readlimit > this.buffer.getCapacity()) {
            throw new IllegalArgumentException("Cannot mark further than the length of the buffer");
        }
        this.markedPosition = this.buffer.readPos;
        this.readLimit = readlimit;
    }

    @Override
    public synchronized void reset() throws IOException {
        if (this.markedPosition < 0) {
            return;
        }
        if (this.markedPosition > this.buffer.writePos) {
            this.buffer.wrapFlag = true;
        }
        this.buffer.readPos = this.markedPosition;
    }

    @Override
    public boolean markSupported() {
        return true;
    }
}

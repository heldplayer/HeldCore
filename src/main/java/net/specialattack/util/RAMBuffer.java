
package net.specialattack.util;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RAMBuffer {

    private final ByteBuffer buffer;
    private int capacity;
    private int readPos;
    private int writePos;
    // True: write has wrapped but read hasn't yet
    // False: write and read have wrapped
    private boolean wrapFlag = false;

    private List<RAMBuffer.Sections> sections;

    private Object readObj = new Object();
    private Object writeObj = new Object();

    public RAMBuffer(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Buffer size must be greater than 1");
        }
        if ((capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException("Buffer size must be a power of 2");
        }
        this.buffer = ByteBuffer.allocate(capacity);
        this.capacity = capacity;
    }

    public void addSection(RAMBuffer.Sections section) {
        if (section.sectionSize > capacity) {
            throw new IllegalArgumentException("Section size must be smaller than or equal to buffer size");
        }

        if (this.sections == null) {
            this.sections = new ArrayList<RAMBuffer.Sections>();
        }

        this.sections.add(section);
    }

    /**
     * Gets the capacity of the RAMBuffer
     * 
     * @return The capacity of the buffer
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Gets the available bytes to be read
     * 
     * @return The amount of available bytes to be read
     */
    public int getAvailable() {
        if (this.wrapFlag) {
            return this.writePos + this.capacity - this.readPos;
        }
        else {
            return this.writePos - this.readPos;
        }
    }

    /**
     * Resets the buffer to re-use it
     */
    public void reset() {
        this.buffer.clear();
    }

    /**
     * Reads from the buffer into the parameter byte array
     * 
     * @param dest
     *        The array to read to
     * 
     * @see read(byte[], int, int)
     */
    public byte[] read(byte[] dest) {
        return this.read(dest, 0, dest.length);
    }

    /**
     * Reads <code>length</code> bytes of the buffer into the parameter byte
     * array, starting at <code>offset</code>
     * 
     * @param dest
     *        The array to read to
     * @param offset
     *        The starting offset to read from the destination array
     * @param length
     *        The amount of bytes to read
     */
    public byte[] read(byte[] dest, int offset, int length) {
        if (length >= this.capacity) {
            throw new IllegalArgumentException("Destination array was bigger than the buffer capacity");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset can't be less than 0");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Length can't be less than 0");
        }
        if (length > dest.length) {
            throw new IllegalArgumentException("Length can't be greater than the destination array");
        }

        // Check to see if reading doesn't surpass the writing
        if (this.readPos + length > this.capacity) {
            if (this.wrapFlag && length - this.capacity + this.readPos > this.writePos) {
                synchronized (this.readObj) {
                    try {
                        this.readObj.wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else {
            if (!this.wrapFlag && this.readPos + length > this.writePos) {
                synchronized (this.readObj) {
                    try {
                        this.readObj.wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (this.sections != null) {
            for (RAMBuffer.Sections section : this.sections) {
                section.onRead(this, length);
            }
        }

        synchronized (this.buffer) {
            // Check for the end of the buffer and wrap around to the start
            if (this.readPos + length > this.capacity) {
                this.buffer.position(this.readPos);
                this.buffer.get(dest, 0, this.capacity - this.readPos);
                this.buffer.rewind();
                this.buffer.get(dest, this.capacity - this.readPos, length - this.capacity + this.readPos);
                this.readPos = length - this.capacity + this.readPos;
                this.wrapFlag = false;
            }
            else {
                this.buffer.position(this.readPos);
                this.buffer.get(dest, offset, length);
                this.readPos += length;
            }
        }

        // Notify the writing object if it is waiting
        synchronized (this.writeObj) {
            this.writeObj.notify();
        }

        return dest;
    }

    /**
     * Skips a certain amount of bytes
     * 
     * @param amount
     *        The amount of bytes to skip
     */
    public void skip(int amount) {
        if (amount >= this.capacity) {
            throw new IllegalArgumentException("Can't skip more than the size of the buffer");
        }

        if (this.sections != null) {
            for (RAMBuffer.Sections section : this.sections) {
                section.onRead(this, amount);
            }
        }

        if (this.readPos + amount > this.capacity) {
            this.readPos = amount - this.capacity + this.readPos;
            this.wrapFlag = false;
        }
        else {
            this.readPos += amount;
        }
    }

    /**
     * Writes the parameter byte array to the buffer
     * 
     * @param src
     *        The array to read from
     */
    public void write(byte[] src) {
        this.write(src, 0, src.length);
    }

    /**
     * Writes <code>length</code> bytes of the parameter byte array into the
     * buffer, starting at <code>offset</code>
     * 
     * @param src
     *        The array to be written from
     * @param offset
     *        The starting offset to write from the destination array
     * @param length
     *        The amount of bytes to write
     */
    public void write(byte[] src, int offset, int length) {
        if (src.length >= this.capacity) {
            throw new IllegalArgumentException("Source array was bigger than the buffer capacity");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset can't be less than 0");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Length can't be less than 0");
        }
        if (length > src.length) {
            throw new IllegalArgumentException("Length can't be greater than the source array");
        }

        // Check to see if writing doesn't overwrite stuff that has yet to be read
        if (this.writePos + length > this.capacity) {
            if (!this.wrapFlag && length - this.capacity + this.writePos > this.readPos) {
                this.wrapFlag = true;
                synchronized (this.writeObj) {
                    try {
                        this.writeObj.wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else {
            if (this.wrapFlag && this.writePos + length > this.readPos) {
                synchronized (this.writeObj) {
                    try {
                        this.writeObj.wait();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        synchronized (this.buffer) {
            // Check for the end of the buffer and wrap around to the start
            if (this.writePos + length > this.capacity) {
                this.buffer.position(this.writePos);
                this.buffer.put(src, 0, this.capacity - this.writePos);
                this.buffer.rewind();
                this.buffer.put(src, this.capacity - this.writePos, length - this.capacity + this.writePos);
                this.writePos = length - this.capacity + this.writePos;
                this.wrapFlag = true;
            }
            else {
                this.buffer.position(this.writePos);
                this.buffer.put(src, offset, length);
                this.writePos += length;
            }
        }

        // Notify the reading object if it is waiting
        synchronized (this.readObj) {
            this.readObj.notify();
        }
    }

    /**
     * Prints the status of the RAMBuffer to the specified PrintStream
     * 
     * @param str
     *        The stream to print to, see {@link System.out} and
     *        {@link System.err}
     */
    @Deprecated
    public void printStatus(PrintStream str) {
        str.println(this.toString() + "; buffer: " + Arrays.toString(this.buffer.array()) + "; sections: " + this.sections.toString());
    }

    @Override
    public String toString() {
        return "capacity: " + this.capacity + "; readPos: " + this.readPos + "; writePos: " + this.writePos + "; wrapFlag: " + this.wrapFlag;
    }

    public static class Sections {

        protected int sectionSize;
        private byte[][] sections;
        protected int lastSectionIndex;
        protected int bytesUntillNextSection;

        public Sections(int sectionSize, int sectionCount) {
            this.sectionSize = sectionSize;
            if (sectionSize > 0 && sectionCount > 0) {
                this.sections = new byte[sectionCount][sectionSize];
            }
        }

        public byte[][] getStoredSections() {
            if (this.sectionSize > 0) {
                return this.sections;
            }
            else {
                throw new IllegalStateException("RAMBuffer doesn't have sections enabled");
            }
        }

        public void setStoredSectionCount(int size) {
            byte[][] temp = this.sections;
            this.sections = new byte[size][sectionSize];
            for (int i = 0; i < temp.length && i < this.sections.length; i++) {
                this.sections[this.sections.length - i - 1] = temp[temp.length - i - 1];
            }
        }

        public void onRead(RAMBuffer buffer, int length) {
            synchronized (buffer.buffer) {
                // Check for the end of the buffer and wrap around to the start

                int temp = length + this.bytesUntillNextSection;
                while (temp >= this.sectionSize) {
                    byte[] last = this.sections[0];
                    System.arraycopy(this.sections, 1, this.sections, 0, this.sections.length - 1);
                    this.sections[this.sections.length - 1] = last;

                    if (this.lastSectionIndex + this.sectionSize > buffer.capacity) {
                        buffer.buffer.position(this.lastSectionIndex);
                        buffer.buffer.get(last, 0, buffer.capacity - this.lastSectionIndex);
                        buffer.buffer.rewind();
                        buffer.buffer.get(last, buffer.capacity - this.lastSectionIndex, this.sectionSize - buffer.capacity + this.lastSectionIndex);
                        this.lastSectionIndex = this.sectionSize - buffer.capacity + this.lastSectionIndex;
                    }
                    else {
                        buffer.buffer.position(this.lastSectionIndex);
                        buffer.buffer.get(last, 0, this.sectionSize);
                        this.lastSectionIndex += this.sectionSize;
                    }

                    this.pushData(last);

                    temp -= this.sectionSize;
                }
                this.bytesUntillNextSection = temp;
            }
        }

        public void pushData(byte[] section) {}

    }

}

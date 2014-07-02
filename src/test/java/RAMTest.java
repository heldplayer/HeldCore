import java.util.Arrays;

import net.specialattack.util.RAMBuffer;

public class RAMTest {

    public static void main(String[] params) {
        testBasicBuffer();
    }

    private static void testBasicBuffer() {
        final RAMBuffer buffer = new RAMBuffer(16);
        new Thread(new Runnable() {
            @Override
            public void run() {
                buffer.write(new byte[] { 1, 2, 3, 4, 5, 6 });
                buffer.printStatus(System.out);
                buffer.write(new byte[] { 3, 5, 9, 1 });
                buffer.printStatus(System.out);
                buffer.write(new byte[] { 9, 1, 6, 4, 6, 1, 6 });
                buffer.printStatus(System.out);
                buffer.write(new byte[] { 8, 9, 1, 6, 4, 3 });
                buffer.printStatus(System.out);
            }
        }).start();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.err.println(Arrays.toString(buffer.read(new byte[6])));
                buffer.printStatus(System.err);
                System.err.println(Arrays.toString(buffer.read(new byte[4])));
                buffer.printStatus(System.err);
                System.err.println(Arrays.toString(buffer.read(new byte[7])));
                buffer.printStatus(System.err);
                System.err.println(Arrays.toString(buffer.read(new byte[6])));
                buffer.printStatus(System.err);
            }
        });
        t.start();
        try {
            t.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

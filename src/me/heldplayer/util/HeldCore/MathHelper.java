
package me.heldplayer.util.HeldCore;

public class MathHelper {

    private static float[] sinTable = new float[65536];

    static {
        for (int i = 0; i < 65536; ++i) {
            sinTable[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
        }
    }

    /**
     * Returns the sin of an angle in Quaternary degrees
     */
    public static final float sin(float angle) {
        return sinTable[(int) (angle * 16384.0F) & 65535];
    }

    /**
     * Returns the sin of an angle in Quaternary degrees
     */
    public static final float cos(float angle) {
        return sinTable[(int) (angle * 16384.0F + 16384.0F) & 65535];
    }

    /**
     * Returns the max of the 2
     */
    public static int max(int number1, int number2) {
        return number1 > number2 ? number1 : number2;
    }

    /**
     * Returns the max of the 2
     */
    public static float max(float number1, float number2) {
        return number1 > number2 ? number1 : number2;
    }

    /**
     * Returns the min of the 2
     */
    public static int min(int number1, int number2) {
        return number1 < number2 ? number1 : number2;
    }

    /**
     * Returns the min of the 2
     */
    public static float min(float number1, float number2) {
        return number1 < number2 ? number1 : number2;
    }

    /**
     * Returns the absolute value
     */
    public static int abs(int number) {
        return number < 0 ? -number : number;
    }

    /**
     * Returns the absolute value
     */
    public static float abs(float number) {
        return number < 0.0F ? -number : number;
    }

    /**
     * Returns the absolute value
     */
    public static double abs(double number) {
        return number < 0.0D ? -number : number;
    }

    public static float lerp(float origin, float target, int steps, int maxSteps) {
        return origin + (target - origin) * (float) steps / (float) maxSteps;
    }

    public static double lerp(double origin, double target, int steps, int maxSteps) {
        return origin + (target - origin) * (double) steps / (double) maxSteps;
    }

    /**
     * Gets a point on a bezier curve
     */
    public static Vector bezier(Vector[] input, double t) {
        if (input.length < 2) {
            throw new RuntimeException("Need more input points");
        }
        if (input.length == 2) {
            Vector[] points = new Vector[2];

            for (int i = 0; i < points.length; i++) {
                points[i] = input[i].clone();
            }

            Vector result = new Vector();
            points[0].multiply(t);
            points[1].multiply(1.0D - t);
            result.add(points[0]);
            result.add(points[1]);

            return result;
        }
        if (input.length == 3) {
            Vector[] points = new Vector[3];

            for (int i = 0; i < points.length; i++) {
                points[i] = input[i].clone();
            }

            Vector result = new Vector();
            points[0].multiply(t * t);
            points[1].multiply((1.0D - t) * t * 2.0D);
            points[2].multiply((1.0D - t) * (1.0D - t));
            result.add(points[0]);
            result.add(points[1]);
            result.add(points[2]);

            return result;
        }
        if (input.length == 4) {
            Vector[] points = new Vector[4];

            for (int i = 0; i < points.length; i++) {
                points[i] = input[i].clone();
            }

            Vector result = new Vector();
            points[0].multiply(t * t * t);
            points[1].multiply((1.0D - t) * t * t * 3.0D);
            points[2].multiply((1.0D - t) * (1.0D - t) * t * 3.0D);
            points[3].multiply((1.0D - t) * (1.0D - t) * (1.0D - t));
            result.add(points[0]);
            result.add(points[1]);
            result.add(points[2]);
            result.add(points[3]);

            return result;
        }

        throw new RuntimeException("Unknown bezier function");
    }

}

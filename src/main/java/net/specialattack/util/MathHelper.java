
package net.specialattack.util;


public final class MathHelper {

    private static float[] sinTable = new float[65536];
    private static int[] bezierValues = new int[16];

    static {
        for (int i = 0; i < 65536; ++i) {
            MathHelper.sinTable[i] = (float) Math.sin(i * Math.PI * 2.0D / 65536.0D);
        }

        MathHelper.bezierValues[0] = 1;
    }

    /**
     * Returns the sin of an angle in Quaternary degrees
     */
    public static float sin(float angle) {
        return MathHelper.sinTable[(int) (angle * 16384.0F) & 65535];
    }

    /**
     * Returns the sin of an angle in Quaternary degrees
     */
    public static float cos(float angle) {
        return MathHelper.sinTable[(int) (angle * 16384.0F + 16384.0F) & 65535];
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

    public static float sqrt(float par0) {
        return (float) Math.sqrt(par0);
    }

    public static float sqrt(double par0) {
        return (float) Math.sqrt(par0);
    }

    public static float lerp(float origin, float target, int steps, int maxSteps) {
        return origin + (target - origin) * steps / maxSteps;
    }

    public static double lerp(double origin, double target, int steps, int maxSteps) {
        return origin + (target - origin) * steps / maxSteps;
    }

    public static double partial(double start, double end, float partial) {
        return start + (end - start) * partial;
    }

    public static float partial(float start, float end, float partial) {
        return start + (end - start) * partial;
    }

    public static int partial(int start, int end, float partial) {
        return (int) (start + (end - start) * partial);
    }

    /**
     * Gets a point on a bezier curve
     */
    public static Vector bezier(Vector[] input, double t) {
        int actualInput = 0;
        for (int i = 0; i < input.length; i++) {
            if (input[i] == null) {
                break;
            }
            actualInput++;
        }

        for (int i = 1; i < MathHelper.bezierValues.length; i++) {
            if (i > actualInput - 1) {
                MathHelper.bezierValues[i] = 0;
            }
            else {
                MathHelper.bezierValues[i] = MathHelper.bezierValues[i - 1] * (actualInput - i) / i;
            }
        }

        Vector[] points = VectorPool.getFreeVectorArray(actualInput);
        Vector result = VectorPool.getFreeVector();

        for (int i = 0; i < actualInput; i++) {
            points[i] = input[i].clone();

            points[i].multiply(MathHelper.bezierValues[i]);

            for (int j = i; j < actualInput - 1; j++) {
                points[i].multiply(t);
            }
            for (int j = 0; j < i; j++) {
                points[i].multiply(1.0D - t);
            }

            result.add(points[i]);
        }

        return result;
    }

}

/* *****************************************************************
 *
 * Copyright 2022 Montera Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *******************************************************************/

package java.lang;

/**
 * @author Greg McCreath
 * @since 0.0.1
 */
public final class Math {

    private Math() {}

    public static double abs(double d) {
        long bits = Double.doubleToLongBits(d);
        bits &= 0x7fffffffffffffffL;
        return Double.longBitsToDouble(bits);
    }

    public static float abs(float f) {
        int bits = Float.floatToIntBits(f);
        bits &= 0x7fffffff;
        return Float.intBitsToFloat(bits);
    }

    public static int abs(int a) {
        return (a < 0) ? -a : a;
    }

    public static long abs(long a) {
        return (a < 0) ? -a : a;
    }

    public static double max(double d1, double d2) {
        if (d1 > d2) {
            return d1;
        }
        if (d1 < d2) {
            return d2;
        }
        /* if either arg is NaN, return NaN */
        if (d1 != d2) {
            return Double.NaN;
        }
        /* max(+0.0,-0.0) == +0.0 */
        /* 0 == Double.doubleToRawLongBits(0.0d) */
//        if (Double.doubleToRawLongBits(d1) != 0) { // gmc
        if (Double.doubleToLongBits(d1) != 0) {
            return d2;
        }
        return 0.0d;
    }

    public static float max(float f1, float f2) {
        if (f1 > f2) {
            return f1;
        }
        if (f1 < f2) {
            return f2;
        }
        /* if either arg is NaN, return NaN */
        if (f1 != f2) {
            return Float.NaN;
        }
        /* max(+0.0,-0.0) == +0.0 */
        /* 0 == Float.floatToRawIntBits(0.0f) */
//        if (Float.floatToRawIntBits(f1) != 0) {
        if (Float.floatToIntBits(f1) != 0) { // gmc
            return f2;
        }
        return 0.0f;
    }


    public static int max(int a, int b) {
        return (a >= b) ? a : b;
    }

    public static long max(long a, long b) {
        return (a >= b) ? a : b;
    }

    public static double min(double d1, double d2) {
        if (d1 > d2) {
            return d2;
        }
        if (d1 < d2) {
            return d1;
        }
        /* if either arg is NaN, return NaN */
        if (d1 != d2) {
            return Double.NaN;
        }
        /* min(+0.0,-0.0) == -0.0 */
        /* 0x8000000000000000L == Double.doubleToRawLongBits(-0.0d) */
//        if (Double.doubleToRawLongBits(d1) == 0x8000000000000000L) {
        if (Double.doubleToLongBits(d1) == 0x8000000000000000L) {
            return -0.0d;
        }
        return d2;
    }

    public static float min(float f1, float f2) {
        if (f1 > f2) {
            return f2;
        }
        if (f1 < f2) {
            return f1;
        }
        /* if either arg is NaN, return NaN */
        if (f1 != f2) {
            return Float.NaN;
        }
        /* min(+0.0,-0.0) == -0.0 */
        /* 0x80000000 == Float.floatToRawIntBits(-0.0f) */
//        if (Float.floatToRawIntBits(f1) == 0x80000000) {
        if (Float.floatToIntBits(f1) == 0x80000000) {
            return -0.0f;
        }
        return f2;
    }

    public static int min(int a, int b) {
        return (a <= b) ? a : b;
    }

    public static long min(long a, long b) {
        return (a <= b) ? a : b;
    }

    public static long round(double d) {
        // check for NaN
        if (d != d) {
            return 0L;
        }
        return (long) floor(d + 0.5d);
    }

    public static int round(float f) {
        // check for NaN
        if (f != f) {
            return 0;
        }
        return (int) floor(f + 0.5f);
    }

    public static native double floor(double d);

    public static native double sqrt(double d);

    public static native double log(double d);

    public static native double log10(double d);

    public static native double ceil(double d);

    public static native double exp(double d);

    public static native double IEEEremainder(double d1, double d2);


}


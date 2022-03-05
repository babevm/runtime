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
public final class Long extends Number {

    public static final long MIN_VALUE = 0x8000000000000000L;

    public static final long MAX_VALUE = 0x7fffffffffffffffL;

	public static final Class<Long> TYPE = (Class<Long>) Class.getPrimitiveClass(Class.LONG_CLASS);

    public static String toString(long i, int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
            radix = 10;

        char[] buf = new char[65];
        int charPos = 64;
        boolean negative = (i < 0);
        long lradix = radix;

    	if (!negative) {
            i = -i;
        }

        while (i <= -lradix) {
            buf[charPos--] = Integer.digits[(int)(-(i % lradix))];
            i = i / lradix;
        }
        buf[charPos] = Integer.digits[(int)(-i)];

        if (negative) {
            buf[--charPos] = '-';
        }

        return new String(buf, charPos, (65 - charPos));
    }

	public static Long valueOf(long l) {
		return new Long(l);
	}

    public static String toString(long i) {
        return toString(i, 10);
    }

    public static long parseLong(String string, int radix)
            throws NumberFormatException {
        if (string == null || radix < Character.MIN_RADIX
                || radix > Character.MAX_RADIX) {
            throw new NumberFormatException();
        }
        int length = string.length();
        int i = 0;
        if (length == 0) {
            throw new NumberFormatException(string);
        }
        boolean negative = string.charAt(i) == '-';
        if (negative && ++i == length) {
            throw new NumberFormatException(string);
        }

        return parse(string, i, radix, negative);
    }

    private static long parse(String string, int offset, int radix,
                              boolean negative) {
        long max = Long.MIN_VALUE / radix;
        long result = 0, length = string.length();
        while (offset < length) {
            int digit = Character.digit(string.charAt(offset++), radix);
            if (digit == -1) {
                throw new NumberFormatException(string);
            }
            if (max > result) {
                throw new NumberFormatException(string);
            }
            long next = result * radix - digit;
            if (next > result) {
                throw new NumberFormatException(string);
            }
            result = next;
        }
        if (!negative) {
            result = -result;
            if (result < 0) {
                throw new NumberFormatException(string);
            }
        }
        return result;
    }


    public static long parseLong(String s) throws NumberFormatException {
      return parseLong(s, 10);
    }

    private long _value;

    public Long(long value) {
        this._value = value;
    }

    public long longValue() {
        return (long)_value;
    }

    public float floatValue() {
        return (float)_value;
    }

    public double doubleValue() {
        return (double)_value;
    }

    public int intValue() {
    	return (int)_value;
    }

    public String toString() {
        return toString(_value, 10);
    }

    public static String toHexString(long l) {
        int count = 1;
        long j = l;

        if (l < 0) {
            count = 16;
        } else {
            while ((j >>= 4) != 0) {
                count++;
            }
        }

        char[] buffer = new char[count];
        do {
            int t = (int) (l & 15);
            if (t > 9) {
                t = t - 10 + 'a';
            } else {
                t += '0';
            }
            buffer[--count] = (char) t;
            l >>= 4;
        } while (count > 0);
        return new String(0, buffer.length, buffer);
	}

    public static String toOctalString(long l) {
        int count = 1;
        long j = l;

        if (l < 0) {
            count = 22;
        } else {
            while ((j >>>= 3) != 0) {
                count++;
            }
        }

        char[] buffer = new char[count];
        do {
            buffer[--count] = (char) ((l & 7) + '0');
            l >>>= 3;
        } while (count > 0);
        return new String(0, buffer.length, buffer);
    }

    public static String toBinaryString(long l) {
        int count = 1;
        long j = l;

        if (l < 0) {
            count = 64;
        } else {
            while ((j >>= 1) != 0) {
                count++;
            }
        }

        char[] buffer = new char[count];
        do {
            buffer[--count] = (char) ((l & 1) + '0');
            l >>= 1;
        } while (count > 0);
        return new String(0, buffer.length, buffer);
    }

    public int hashCode() {
        return (int)(_value ^ (_value >> 32));
    }

    public boolean equals(Object obj) {
        if (obj instanceof Long) {
            return _value == ((Long)obj).longValue();
        }
        return false;
    }

}


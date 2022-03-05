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
public final class Integer extends Number {

	public static final int MIN_VALUE = 0x80000000;
	public static final int MAX_VALUE = 0x7fffffff;
	private final int _value;

	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z' };

	public static final Class<Integer> TYPE = (Class<Integer>) Class.getPrimitiveClass(Class.INTEGER_CLASS);

	public Integer(int value) {
		this._value = value;
	}

	public static String toString(int i, int radix) {

		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
			radix = 10;

		/* dp native method if radix 10 */
		if (radix == 10) {
			return toString(i);
		}

		char buf[] = new char[33];
		boolean negative = (i < 0);
		int charPos = 32;

		if (!negative) {
			i = -i;
		}

		while (i <= -radix) {
			buf[charPos--] = digits[-(i % radix)];
			i = i / radix;
		}
		buf[charPos] = digits[-i];

		if (negative) {
			buf[--charPos] = '-';
		}

		return new String(buf, charPos, (33 - charPos));
	}

	public static native String toString(int i);

	public static int parseInt(String s) throws NumberFormatException {
		return parseInt(s, 10);
	}

	public static int parseInt(String string, int radix) throws NumberFormatException {

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

	private static int parse(String string, int offset, int radix,
							 boolean negative) throws NumberFormatException {
		int max = Integer.MIN_VALUE / radix;
		int result = 0;
		int length = string.length();
		while (offset < length) {
			int digit = Character.digit(string.charAt(offset++), radix);
			if (digit == -1) {
				throw new NumberFormatException(string);
			}
			if (max > result) {
				throw new NumberFormatException(string);
			}
			int next = result * radix - digit;
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

	public static String toHexString(int i) {
		int count = 1, j = i;

		if (i < 0) {
			count = 8;
		} else {
			while ((j >>>= 4) != 0) {
				count++;
			}
		}

		char[] buffer = new char[count];
		do {
			int t = i & 15;
			if (t > 9) {
				t = t - 10 + 'a';
			} else {
				t += '0';
			}
			buffer[--count] = (char) t;
			i >>>= 4;
		} while (count > 0);
		return new String(0, buffer.length, buffer);
	}

	public static String toBinaryString(int i) {
		int count = 1, j = i;

		if (i < 0) {
			count = 32;
		} else {
			while ((j >>>= 1) != 0) {
				count++;
			}
		}

		char[] buffer = new char[count];
		do {
			buffer[--count] = (char) ((i & 1) + '0');
			i >>>= 1;
		} while (count > 0);
		return new String(0, buffer.length, buffer);
	}

	public static String toOctalString(int i) {
		int count = 1, j = i;

		if (i < 0) {
			count = 11;
		} else {
			while ((j >>>= 3) != 0) {
				count++;
			}
		}

		char[] buffer = new char[count];
		do {
			buffer[--count] = (char) ((i & 7) + '0');
			i >>>= 3;
		} while (count > 0);
		return new String(0, buffer.length, buffer);
	}

	public static Integer valueOf(String s) throws NumberFormatException {
		return new Integer(parseInt(s,10));
	}

	public static Integer valueOf(String s, int radix) throws NumberFormatException {
		return new Integer(parseInt(s,radix));
	}

	public static Integer valueOf(int i) {
		return new Integer(i);
	}

	public int value() {
		return _value;
	}

	public byte byteValue() {
		return (byte) _value;
	}

	public short shortValue() {
		return (short) _value;
	}

	public int intValue() {
		return _value;
	}

	public long longValue() {
		return (long) _value;
	}

	public float floatValue() {
		return (float) _value;
	}

	public double doubleValue() {
		return (double) _value;
	}

	public String toString() {
		return String.valueOf(_value);
	}

	public int hashCode() {
		return _value;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Integer) {
			return _value == ((Integer) obj)._value;
		}
		return false;
	}

}

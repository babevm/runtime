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
public final class Short extends Number {

	private short _value;

	public static final short MIN_VALUE = -32768;

	public static final short MAX_VALUE = 32767;

	public static short parseShort(String s) throws NumberFormatException {
		int i = Integer.parseInt(s,10);

		if (i < MIN_VALUE || i > MAX_VALUE)
			throw new NumberFormatException();

		return (short) i;
	}

	public static short parseShort(String s, int radix) throws NumberFormatException {
		int i = Integer.parseInt(s, radix);

		if (i < MIN_VALUE || i > MAX_VALUE)
			throw new NumberFormatException();

		return (short) i;
	}

	public Short(short value) {
		this._value = value;
	}

	public static final Class<Short> TYPE = (Class<Short>) Class.getPrimitiveClass(Class.SHORT_CLASS);

	public short value() {
		return _value;
	}

	public byte byteValue() {
		return (byte) _value;
	}

	public short shortValue() {
		return _value;
	}

	public int intValue() {
		return (int) _value;
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

	public static Short valueOf(short s) {
		return new Short(s);
	}

	public String toString() {
		return String.valueOf((int) _value);
	}

	public int hashCode() {
		return (int) _value;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Short) {
			return _value == ((Short) obj).value();
		}
		return false;
	}

}

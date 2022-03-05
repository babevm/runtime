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
public final class Byte extends Number {

	private final byte _value;

	public static final byte MIN_VALUE = -128;

	public static final byte MAX_VALUE = 127;

	public Byte(byte value) {
		this._value = value;
	}

	public static byte parseByte(String s) throws NumberFormatException {
		return parseByte(s,10);
	}

	public static byte parseByte(String s, int radix) throws NumberFormatException {
		int i = Integer.parseInt(s, radix);
		if ((byte) i != i)
			throw new NumberFormatException();
		return (byte) i;
	}

	public static final Class<Byte> TYPE = (Class<Byte>) Class.getPrimitiveClass(Class.BYTE_CLASS);

	public byte value() {
		return _value;
	}

	public byte byteValue() {
		return _value;
	}

	public short shortValue() {
		return (short) _value;
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

	public static Byte valueOf(byte b) {
		return new Byte(b);
	}

	public String toString() {
		return String.valueOf((int) _value);
	}

	public int hashCode() {
		return (int) _value;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Byte) {
			return _value == ((Byte) obj)._value;
		}
		return false;
	}

}

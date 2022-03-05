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
public final class Double extends Number {

	public static final double POSITIVE_INFINITY = 1.0 / 0.0;
	public static final double NEGATIVE_INFINITY = -1.0 / 0.0;
	public static final double NaN = 0.0d / 0.0;
	public static final double MAX_VALUE = 0x1.fffffffffffffP+1023;
	public static final double MIN_VALUE = 0x0.0000000000001P-1022;

	private final double _value;

	public Double(double value) {
		this._value = value;
	}

	public String toString() {
		return Double.toString(_value);
	}

	public static native String toString(double d);

	public static Double valueOf(String s) throws NumberFormatException {
		throw new NumberFormatException("Not yet implemented");
	}

	public static Double valueOf(double d) {
		return new Double(d);
	}

	public double value() {
		return _value;
	}

	public byte byteValue() {
		return (byte) _value;
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
		return _value;
	}

	public int hashCode() {
		long bits = doubleToLongBits(_value);
		return (int)(bits ^ (bits >>> 32));
	}

	public static boolean isNaN(double v) {
		return (v != v);
	}

	public boolean isNaN() {
		return isNaN(_value);
	}

	static public boolean isInfinite(double v) {
		return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
	}

	public static final Class<Double> TYPE = (Class<Double>) Class.getPrimitiveClass(Class.DOUBLE_CLASS);

	public boolean equals(Object obj) {
		if (obj instanceof Double) {
			return (doubleToLongBits(((Double) obj)._value) == doubleToLongBits(_value));
		}
		return false;
	}

	public static native long doubleToLongBits(double value);

	public static native double longBitsToDouble(long bits);
}

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
public final class Float extends Number {

	public static final float POSITIVE_INFINITY = 1.0f / 0.0f;
	public static final float NEGATIVE_INFINITY = -1.0f / 0.0f;
	public static final float NaN = 0.0f / 0.0f;
	public static final float MAX_VALUE = 3.40282346638528860e+38f;
	public static final float MIN_VALUE = 1.40129846432481707e-45f;

	private final float _value;

	public Float(float value) {
		this._value = value;
	}

	public String toString() {
		return Float.toString(_value);
	}

	public static native String toString(float f);

	public static Float valueOf(String s) throws NumberFormatException {
		throw new NumberFormatException("Not yet implemented");
	}

	public static Float valueOf(float f) {
		return new Float(f);
	}

	public float value() {
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
		return _value;
	}

	public double doubleValue() {
		return (double) _value;
	}

	public int hashCode() {
		return floatToIntBits(_value);
	}

	public static boolean isNaN(float v) {
		return (v != v);
	}

	public boolean isNaN() {
		return isNaN(_value);
	}

	static public boolean isInfinite(float v) {
		return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
	}

	public static final Class<Float> TYPE = (Class<Float>) Class.getPrimitiveClass(Class.FLOAT_CLASS);

	public boolean equals(Object obj) {
		if (obj instanceof Float) {
			return (floatToIntBits(((Float) obj)._value) == floatToIntBits(_value));
		}
		return false;
	}

	public static native int floatToIntBits(float value);

	public static native float intBitsToFloat(int bits);
}

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
public final class Boolean {

	public static final Boolean TRUE = new Boolean(true);

	public static final Boolean FALSE = new Boolean(false);

	private final boolean _value;

	public Boolean(boolean value) {
		this._value = value;
	}

    public static final Class<Boolean> TYPE = (Class<Boolean>) Class.getPrimitiveClass(Class.BOOLEAN_CLASS);

	public boolean value() {
		return _value;
	}

	public boolean booleanValue() {
		return _value;
	}

	public static Boolean valueOf(boolean bool) {
		return bool ? TRUE : FALSE;
	}

	public String toString() {
		return _value ? "true" : "false";
	}

	public static String toString(boolean b) {
		return b ? "true" : "false";
	}

	public int hashCode() {
		return _value ? 1231 : 1237;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Boolean) {
			return _value == ((Boolean) obj)._value;
		}
		return false;
	}

}

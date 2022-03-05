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
public abstract class Enum<E extends Enum<E>> implements Comparable<E> {

	private final String name;

	public final String name() {
		return name;
	}

	private final int ordinal;

	public final int ordinal() {
		return ordinal;
	}

	protected Enum(String name, int ordinal) {
		this.name = name;
		this.ordinal = ordinal;
	}

	public String toString() {
		return name;
	}

	public final boolean equals(Object other) {
		return this == other;
	}

	public final int hashCode() {
		return super.hashCode();
	}

	protected final Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public final int compareTo(E o) {
		Enum<?> other = (Enum<?>)o;
		return this.ordinal - other.ordinal;
	}

	public final Class<E> getDeclaringClass() {
		Class clazz = getClass();
		Class zuper = clazz.getSuperclass();
		return (zuper == Enum.class) ? clazz : zuper;
	}

	public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
		throw new UnsupportedOperationException("Enum.valueOf not yet implemented");
	}

}

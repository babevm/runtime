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

package java.util;

/**
 * @author Greg McCreath
 * @since 0.0.1
 */
public class Vector<E> {

	protected static final int DEFAULT_CAPACITY = 10;

	protected static final int DEFAULT_INCREMENT = 5;

	protected int capacityIncrement;

	protected int elementCount;

	protected E[] elementData;

	/**
	 * Create a new List using the default starting size and growth increment.
	 */
	public Vector() {
		this(DEFAULT_CAPACITY, DEFAULT_INCREMENT);
	}

	/**
	 * Create a new List specifying an initial size.
	 *
	 * @param capacity
	 *            the initial size of the list.
	 */
	public Vector(int capacity) {
		this(capacity, DEFAULT_INCREMENT);
	}

	/**
	 * Create a new List specifying initial size and growth increment.
	 *
	 * @param capacity
	 * @param increment
	 */
	public Vector(int capacity, int increment) {

		if (capacity < 0)
			throw new IllegalArgumentException();

		capacityIncrement = increment;
		elementData = (E[]) new Object[capacity];
		elementCount = 0;
	}

	public int capacity() {
		return elementData.length;
	}

	/** Adds an object to the end of the vector. */
	public void addElement(E obj) {
		if (elementCount < elementData.length)
			elementData[elementCount++] = obj;
		else
			insertElementAt(obj, elementCount);
	}

	/** Inserts an object at the given index. */
	public void insertElementAt(E obj, int index) {
		if (elementCount == elementData.length) {
			resizeTo(elementData.length + capacityIncrement);
		}
		if (index != elementCount) {
			System.arraycopy(elementData, index, elementData, index + 1, elementCount - index);
		}

		elementData[index] = obj;
		elementCount++;
	}

	/** Removes the object reference at the given index. */
	public void removeElementAt(int index) {

		if ((index < 0) || (index >= elementCount))
			throw new ArrayIndexOutOfBoundsException();

		if (index != elementCount - 1)
			System.arraycopy(elementData, index + 1, elementData, index, elementCount - index - 1);

		elementData[--elementCount] = null;
	}

	public int indexOf(Object obj) {
		return indexOf(obj, 0);
	}

	public boolean contains(Object obj) {
		return (indexOf(obj, 0) != -1);
	}

	public int indexOf(Object obj, int offset) {

		/* TODO, first do a native check by object pointer equality - suffer an extra
		 * method call on the basis that it might save a lot of equals() method calls. */

		if (obj == null) {
			for (int i = offset; i < elementCount; i++)
				if (elementData[i] == null)
					return i;
		} else {
			for (int i = offset; i < elementCount; i++)
				if (obj.equals(elementData[i]))
					return i;
		}
		return -1;
	}

	public void copyInto(E[] anArray) {
		System.arraycopy(elementData, 0, anArray, 0, elementCount);
	}

	public E elementAt(int index) {
		if (index < 0 || index >= elementCount)
			throw new ArrayIndexOutOfBoundsException();

		return (E) elementData[index];
	}

	public Enumeration<E> elements() {
		return new Enumeration<E>() {

			int count = 0;

			public boolean hasMoreElements() {
				return count < elementCount;
			}

			public E nextElement() {
				if (count < elementCount) {
					return (E) elementData[count++];
				}
				throw new NoSuchElementException("Vector Enumeration");
			}
		};
	}

	private void resizeTo(int minCapacity) {
		int oldCapacity = elementData.length;
		Object oldData[] = elementData;
		int newCapacity = (capacityIncrement > 0) ? (oldCapacity + capacityIncrement) : (oldCapacity == 0) ? DEFAULT_INCREMENT
				: (oldCapacity * 2);
		if (newCapacity < minCapacity) {
			newCapacity = minCapacity;
		}
		elementData = (E[]) new Object[newCapacity];
		System.arraycopy(oldData, 0, elementData, 0, elementCount);

	}

	public void ensureCapacity(int minCapacity) {
		if (elementData.length < minCapacity)
			resizeTo(minCapacity);
	}

	public E firstElement() {
		if (elementCount == 0)
			throw new NoSuchElementException();

		return elementData[0];

	}

	public E lastElement() {
		if (elementCount == 0)
			throw new NoSuchElementException();

		return elementData[elementCount - 1];

	}

	public boolean isEmpty() {
		return (elementCount == 0);
	}

	public int lastIndexOf(Object obj) {
		return lastIndexOf(obj, elementCount - 1);

	}

	public int lastIndexOf(Object obj, int index) {

		if (index >= elementCount)
			throw new IndexOutOfBoundsException();

		if (obj == null) {
			for (int i = index; i >= 0; i--)
				if (elementData[i] == null)
					return i;
		} else {
			for (int i = index; i >= 0; i--)
				if (obj.equals(elementData[i]))
					return i;
		}
		return -1;
	}

	public boolean removeElement(Object obj) {
		int i = indexOf(obj, 0);
		if (i >= 0) {
			removeElementAt(i);
			return true;
		}
		return false;
	}

	public void removeAllElements() {
		elementData = (E[]) new Object[DEFAULT_CAPACITY];
		elementCount = 0;
	}

	public void setElementAt(E obj, int index) {

		if ((index < 0) || (index >= elementCount))
			throw new ArrayIndexOutOfBoundsException();

		elementData[index] = obj;
	}

	public void setSize(int newSize) {

		if (newSize < 0)
			throw new ArrayIndexOutOfBoundsException();

		if ((newSize > elementCount) && (newSize > elementData.length)) {
			resizeTo(newSize);
		} else {
			for (int i = newSize; i < elementCount; i++) {
				elementData[i] = null;
			}
		}
		elementCount = newSize;
	}

	public int size() {
		return elementCount;
	}

	public String toString() {
		int max = elementCount - 1;
		StringBuffer buf = new StringBuffer();
		buf.append("[");

		for (int i = 0; i < elementCount; i++) {
			buf.append(elementData[i]);
			if (i < max) {
				buf.append(", ");
			}
		}
		buf.append("]");
		return buf.toString();
	}

	public void trimToSize() {
		int oldCapacity = elementData.length;
		if (elementCount < oldCapacity) {
			Object oldData[] = elementData;
			elementData = (E[]) new Object[elementCount];
			System.arraycopy(oldData, 0, elementData, 0, elementCount);
		}
	}

}

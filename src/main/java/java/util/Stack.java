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
public class Stack<E> extends Vector<E> {

	public Stack() {
		super(DEFAULT_CAPACITY, DEFAULT_INCREMENT);
	}

	/**
	 * Pushes an item onto the top of this stack. This has exactly the same
	 * effect as: <blockquote>
	 *
	 * <pre>
	 * add(item)
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * @param item the item to be pushed onto this stack.
	 * @return the <code>item</code> argument.
	 */
	public E push(E item) {
		addElement(item);
		return item;
	}

	/**
	 * Removes the object at the top of this stack and returns that object as
	 * the value of this function.
	 *
	 * @return The object at the top of this stack (the last item of the
	 *         <tt>Vector</tt> object).
	 * @exception EmptyStackException if this stack is empty.
	 */
	public E pop() {

		E obj;

		if (elementCount == 0)
			throw new EmptyStackException();

		obj = elementData[--elementCount];
		elementData[elementCount] = null;

		return obj;
	}

	/**
	 * Looks at the object at the top of this stack without removing it from the
	 * stack.
	 *
	 * @return the object at the top of this stack (the last item of the
	 *         <tt>Vector</tt> object).
	 * @exception EmptyStackException if this stack is empty.
	 */
	public E peek() {
		if (elementCount == 0)
			throw new EmptyStackException();

		return elementData[elementCount - 1];
	}

	/**
	 * Tests if this stack is empty.
	 *
	 * @return <code>true</code> if and only if this stack contains no items;
	 *         <code>false</code> otherwise.
	 */
	public boolean empty() {
		return elementCount == 0;
	}

	/**
	 * Returns the 1-based position where an object is on this stack. If the
	 * object <tt>o</tt> occurs as an item in this stack, this method returns
	 * the distance from the top of the stack of the occurrence nearest the top
	 * of the stack; the topmost item on the stack is considered to be at
	 * distance <tt>1</tt>. The <tt>equals</tt> method is used to compare
	 * <tt>o</tt> to the items in this stack.
	 *
	 * @param o the desired object.
	 * @return the 1-based position from the top of the stack where the object
	 *         is located; the return value <code>-1</code> indicates that the
	 *         object is not on the stack.
	 */
	public int search(Object o) {
		int i = lastIndexOf(o);

		if (i >= 0) {
			return elementCount - i;
		}

		return -1;
	}

}

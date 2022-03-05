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
 * Iterator interface for traversing collection type objects.
 *
 * @author Greg McCreath
 * @since 0.0.1
 */
public interface Iterator<T> {

	/**
	 * Returns true if the iteration has more elements. (In other words, returns
	 * true if next would return an element rather than throwing an exception.)
	 *
	 * @return true if the iterator has more elements.
	 */
	boolean hasNext();

	/**
	 * Returns the next element in the iteration. Calling this method repeatedly
	 * until the hasNext() method returns false will return each element in the
	 * underlying collection exactly once.
	 *
	 * @return the next element in the iteration.
	 * @throws NoSuchElementException - iteration has no more elements.
	 */
	T next() throws NoSuchElementException;

	void remove();

}
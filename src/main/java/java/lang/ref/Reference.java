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

package java.lang.ref;

/**
 * Abstract base class for reference objects. This class defines the operations
 * common to all reference objects. Because reference objects are implemented in
 * close cooperation with the garbage collector, this class may not be
 * sub-classed directly.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 * @param <T> the type of the object referred to by this WeakReference.
 */
public abstract class Reference<T> {

	// **********************************************************
	// VM assumes these attributes. Do not insert any attributes above
	// this.
	private T referent;

	private int next;

	// **********************************************************

	/**
	 * Return the object referred to by this weak reference. This reference will
	 * be null of the object has been garbage collected.
	 *
	 * @return the referant.
	 */
	public T get() {
		return this.referent;
	}

	/**
	 * Sets the referred-to object to <code>null</code>.
	 */
	public void clear() {
		this.referent = null;
	}

	/**
	 * Create a new reference object for the given referent.
	 *
	 * @param referent a given referent.
	 */
	Reference(T referent) {
		this.referent = referent;
	}

}

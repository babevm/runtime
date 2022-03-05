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
 *
 * This class provides support for weak references. During garbage collection if
 * the referenced object of a WeakReference is no longer reachable by
 * anything other than WeakReferences object the reference of WeakReference
 * objects pointing to it will become <code>null</code>.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 * @param <T> the type of the object referred to by this WeakReference.
 */
public final class WeakReference<T> extends Reference<T> {

	/**
	 * Create a new WeakReference object for the given referent.
	 *
	 * @param referent a given referent.
	 */
	public WeakReference(T referent) {
		super(referent);
		makeweak();
	}

	/**
	 * Native call to inform the VM this is a weak reference object.
	 */
	private native void makeweak();

}

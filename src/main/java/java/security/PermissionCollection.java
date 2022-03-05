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

package java.security;

import java.util.Enumeration;

/**
 * <p>Represents a collection of Permission objects.
 *
 * <p>When a homogeneous collection of permission object is desired the
 * {@link Permission#newPermissionCollection()} method on a Permission object
 * can be called to provide a collection that will correctly implement the
 * implies() method for that collection.
 *
 * <p>A PermissionCollection may be set as read-only using {@link #setReadOnly()}.
 * Attempting to add a new permission to the collection will cause an exception.
 * This is not enforced by this base base, but must be enforced by
 * PermissionCollection subclasses.
 *
 * <p>PermissionCollection objects are not thread safe. Subclasses are to provide
 * their own synchronisation.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public abstract class PermissionCollection {

	boolean _readOnly;

	/**
	 * Adds a Permission to the collection. Attempting to add a permission to a
	 * read-only collection will throw a SecurityError.
	 *
	 * @param permission the permission the add.
	 */
	public abstract void add(Permission permission);

	/**
	 * Checks to see if the specified permission is implied by the collection of
	 * Permission objects held in this PermissionCollection.
	 *
	 * @param permission the permission to compare
	 * @return <code>true</code> if the given permission is implied by the
	 *         permissions in this collection or <code>false</code> otherwise.
	 */
	public abstract boolean implies(Permission permission);

	/**
	 * Returns an enumeration of all the Permission objects in the collection.
	 *
	 * @return enumeration of all the Permissions.
	 */
	public abstract Enumeration<Permission> elements();

	/**
	 * Marks this PermissionCollection object as "readonly". After a
	 * PermissionCollection object is marked as readonly, no new Permission
	 * objects can be added to it using add.
	 */
	public final void setReadOnly() {
		_readOnly = true;
	}

	/**
	 * Returns true if this PermissionCollection object is marked as readonly.
	 * If it is readonly, no new Permission objects can be added to it using
	 * <code>add</code>.
	 *
	 * By default, the object is not readonly. It can be set to readonly by a
	 * call to setReadOnly.
	 *
	 * @return true if this PermissionCollection object is marked as readonly,
	 *         false otherwise.
	 */
	public final boolean isReadOnly() {
		return _readOnly;
	}

}

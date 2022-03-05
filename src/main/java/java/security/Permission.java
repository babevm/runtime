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

/**
 * <p>An abstract class representing access to a system resource. Permission
 * objects are used in security policy definition as well as when checking for
 * actions permission.
 *
 * <p>Permission objects have a mandatory name and an optional integer mask of
 * actions.
 *
 * <p>The meaning of a Permission's name and actions are dependent on the subclass
 * implementation.
 *
 * <p>A number of default actions can be found here as
 * <code>ACTION_</code> public constants.
 *
 * <p>Permissions are immutable and subclasses must respect this and provide no
 * methods for altering the state of a permission.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public abstract class Permission {

	public final static int ACTION_NONE = 0x0;
	public final static int ACTION_READ = 1 << 0;
	public final static int ACTION_WRITE = 1 << 1;
	public final static int ACTION_OPEN = 1 << 2;
	public final static int ACTION_CLOSE = 1 << 3;
	public final static int ACTION_CREATE = 1 << 4;
	public final static int ACTION_DELETE = 1 << 5;
	public final static int ACTION_MOVE = 1 << 6;
	public final static int ACTION_APPEND = 1 << 7;
	public final static int ACTION_RENAME = 1 << 8;
	public final static int ACTION_UPDATE = 1 << 9;
	public final static int ACTION_ALL = 0xFFFFFFFF; /* all 1's */

	/* the permission name */
	String _name;

	/* the optional permission actions bitmap */
	int _actions;

	/**
	 * Creates a new Permission of a given name.
	 *
	 * @param name the permission name. Must be not <code>null</code>.
	 */
	public Permission(String name) {
		this(name, 0);
	}

	/**
	 * Creates a new Permission of a given name and actions.
	 *
	 * @param name the permission name. Must be not <code>null</code>.
	 * @param actions the permission actions.
	 */
	public Permission(String name, int actions) {

		if ((name == null) || name.length() == 0)
			throw new NullPointerException("name can't be null or empty");

		_name = name;
		_actions = actions;
	}

	/**
	 * Checks if the specified permission's actions are "implied by" this
	 * object's actions. This must be implemented by subclasses of Permission,
	 * as they are the only ones that can impose semantics on a Permission
	 * object.
	 *
	 * The implies method is used by the {@link SecurityManager} to determine
	 * whether or not a requested permission is implied by another permission
	 * that is known to be valid in the current execution context.
	 *
	 * @param permission
	 * @return <code>true</code> if this permission object implies the given
	 *         permission object, <code>false</code> otherwise.
	 */
	public abstract boolean implies(Permission permission);

	/**
	 * Checks two Permission object for equality.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public abstract boolean equals(Object obj);

	/**
	 * Returns the hashcode of a permission. The default implementation of a
	 * permission hashcode is based solely on the hashcode of its name.
	 *
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.getName().hashCode();
	}

	/**
	 * Returns the name of a Permission.
	 *
	 * @return the name of a Permission.
	 */
	public final String getName() {
		return _name;
	}

	/**
	 * Returns the actions of a Permission.
	 *
	 * @return the actions of a Permission.
	 */
	public final int getActions() {
		return _actions;
	}

	/**
	 * Returns a PermissionCollection for holding a collection of like
	 * permission. The returned permission collection must implement the
	 * semantics of the "implies" method for determining if a given permission
	 * is implied by the collection.
	 *
	 * Typically, a PermissionCollection is used by the {@link Permissions} class
	 * to hold permission object of the same type together.
	 *
	 * The default implementation returns <code>null</code>.
	 *
	 * @return
	 */
	public PermissionCollection newPermissionCollection() {
		return null;
	}

	/**
	 * Returns a String representation of the Permission actions. Subclasses can
	 * override this method to create a custom string representation - the
	 * default provides a binary String of ones and zeros.
	 *
	 * @return A String representation of the Permission actions.
	 */
	public String getActionsString() {
		return Integer.toString(_actions, 2);
	}

	/**
	 *
	 * Returns a string describing this Permission. The convention is to specify
	 * the class name, the permission name, and the actions in the following
	 * format: '("ClassName" "name" "actionString")'.
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String actions = getActionsString();
		if ((_actions == 0) || (actions.length() == 0)) {
			return "(" + getClass().getName() + " " + _name + ")";
		} else {
			return "(" + getClass().getName() + " " + _name + " " + actions + ")";
		}
	}
}

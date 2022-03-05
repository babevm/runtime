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
import java.util.Hashtable;

/**
 * <p>A PermissionCollection subclass for holding instances of subclasses of
 * BasicPermission. Only a single class of instance can be held in the
 * collection, and this class is given a constructor argument. Any attempt to
 * add a permission of any other type will cause an exception.
 *
 * <p>Adding a permission with a name of "*" will signify that all permissions of
 * the collection type are implied.
 *
 * <p>It is important to note that as part of the collection implies logic the
 * implies() method of individual permission held in the collection is *not*
 * called. The implies logic here matches on name and actions.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public final class BasicPermissionCollection extends PermissionCollection {

	private Hashtable<String, Permission> _permissions;

	// has "* been added?
	private boolean _allAllowed;

	// the class type of the collection.
	private Class<? extends BasicPermission> _class;

	/**
	 * Creates a new BasicPermissionCollection.
	 *
	 * @param cl the class of Permission objects held in the collection .
	 */
	public BasicPermissionCollection(Class<? extends BasicPermission> cl) {
		_class = cl;
		_permissions = new Hashtable<String, Permission>();
		_allAllowed = false;
	}

	public Enumeration<Permission> elements() {
		return _permissions.elements();
	}

	public void add(Permission permission) {

		if (permission.getClass() != _class)
			throw new IllegalArgumentException("invalid permission: " + permission);

		if (_readOnly)
			throw new SecurityException("readonly PermissionCollection");

		String name = permission._name;

		synchronized (this) {
			Permission existing = _permissions.get(name);

			/*
			 * if there is a permission of the same name already there, create a
			 * new one with the sum of the existing actions and the new actions.
			 * Note we do not use a permission of the collection class type, no
			 * need. This collection does not call implies(), or any other
			 * method on a held permission, and we are only interested in the
			 * name and the actions, so a simple new BasicPermission object will
			 * do just fine. It is a bit of a trick to get around figuring out
			 * how to instantiate a class with constructor arguments without
			 * reflection!
			 */
			if (existing != null) {
				_permissions.put(name, new BasicPermission(name, existing._actions | permission._actions));
			} else {
				_permissions.put(name, permission);
			}
		}

		if (!_allAllowed) {
			if (name.equals("*"))
				_allAllowed = true;
		}
	}

	public boolean implies(Permission permission) {

		if (permission.getClass() != _class)
			return false;

		Permission x;

		int desired = permission._actions;
		int effective = 0;

		if (_allAllowed) {
			synchronized (this) {
				x = _permissions.get("*");
			}
			if (x != null) {
				effective |= x._actions;
				if ((effective & desired) == desired)
					return true;
			}
		}

		String name = permission._name;

		synchronized (this) {
			x = _permissions.get(name);
		}

		if (x != null) {
			effective |= x._actions;
			if ((effective & desired) == desired)
				return true;
		}

		int last, offset;

		offset = name.length() - 1;

		while ((last = name.lastIndexOf('.', offset)) != -1) {

			name = name.substring(0, last + 1) + "*";

			synchronized (this) {
				x = _permissions.get(name);
			}

			if (x != null) {
				effective |= x._actions;
				if ((effective & desired) == desired)
					return true;
			}

			offset = last - 1;
		}

		return false;
	}

}

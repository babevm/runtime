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
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * <p>
 * A heterogeneous collection of Permission objects. A Permissions object
 * effectively maintains a set of PermissionCollection objects - one for each
 * permission type added.
 *
 * <p>
 * If an {@link AllPermission} permission is added, only that permission will
 * exist in the collection - all previous permission will be cleared, and all
 * subsequent additions will be ignored.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public final class Permissions extends PermissionCollection {

	/* a map of the held permission - by class */
	private Hashtable<Class<?>, PermissionCollection> _pcMap = new Hashtable<Class<?>, PermissionCollection>();

	/* has an AllPermission been added? */
	boolean _hasAllPermissions;

	public void add(Permission permission) {

		if (_readOnly)
			throw new SecurityException("readonly Permissions");

		/* short circuit if all permission */
		if (_hasAllPermissions)
			return;

		/*
		 * if the permission is an AllPermission, remove all the other
		 * permissions from the permissions map and just keep this single
		 * AllPermission - none of the others matter anymore.
		 */
		if (permission instanceof AllPermission) {
			_hasAllPermissions = true;
			_pcMap.clear();
		}

		getPermissionCollection(permission, true).add(permission);
	}

	/**
	 * Adds the permissions from another PermissionCollection to this
	 * Permissions object.
	 *
	 * If the current Permissions object has an AllPermission this will have no
	 * effect.
	 *
	 * @param pc
	 */
	public void add(PermissionCollection pc) {

		if (_readOnly)
			throw new SecurityException("readonly Permissions");

		/* short circuit if all permission */
		if (_hasAllPermissions)
			return;

		Enumeration<Permission> en = pc.elements();

		while (en.hasMoreElements()) {
			add(en.nextElement());
		}
	}

	/**
	 * Gets a PermissionCollection object for the given Permission.
	 *
	 * @param p a given Permission
	 * @param createEmpty if true, and no PermissionCollection object is cached
	 *            for the given permission, one will be created - first by
	 *            calling {@link Permission#newPermissionCollection()} and that
	 *            returns null by instantiating a new PermissionsVector.
	 * @return a PermissionCollection object for the given Permission or null.
	 */
	private synchronized PermissionCollection getPermissionCollection(Permission p, boolean createEmpty) {

		Class<?> c = p.getClass();

		PermissionCollection pc = (PermissionCollection) _pcMap.get(c);

		if (/* !hasUnresolved && */!createEmpty) {
			return pc;
		} else if (pc == null) {

			// Check for unresolved permissions
			// pc = (hasUnresolved ? getUnresolvedPermissions(p) : null);

			// if still null, create a new collection
			if (pc == null && createEmpty) {

				pc = p.newPermissionCollection();

				// still no PermissionCollection?
				// We'll give them a default.
				if (pc == null)
					pc = new DefaultPermissionCollection();
			}

			if (pc != null) {
				_pcMap.put(c, pc);
			}
		}
		return pc;
	}

	public synchronized Enumeration<Permission> elements() {
		return new PermissionsEnumerator(_pcMap.elements());
	}

	public synchronized boolean implies(Permission permission) {
		if (_hasAllPermissions)
			return true;

		PermissionCollection pc = getPermissionCollection(permission, false);
		return (pc != null) ? pc.implies(permission) : false;
	}
}

/**
 * Enumeration implementation that acts across an enumeration of
 * PermissionCollection objects.
 *
 * @author Greg McCreath
 * @since 0.0.1
 */
final class PermissionsEnumerator implements Enumeration<Permission> {

	private Enumeration<PermissionCollection> _all;
	private Enumeration<Permission> _current;

	PermissionsEnumerator(Enumeration<PermissionCollection> e) {
		_all = e;
		_current = getNextEnumWithMore();
	}

	public boolean hasMoreElements() {

		if (_current == null)
			return false;

		if (_current.hasMoreElements())
			return true;

		_current = getNextEnumWithMore();

		return (_current != null);
	}

	public Permission nextElement() {
		if (hasMoreElements()) {
			return _current.nextElement();
		} else {
			throw new NoSuchElementException("PermissionsEnumerator");
		}

	}

	private Enumeration<Permission> getNextEnumWithMore() {
		while (_all.hasMoreElements()) {
			PermissionCollection pc = (PermissionCollection) _all.nextElement();
			Enumeration<Permission> next = pc.elements();
			if (next.hasMoreElements())
				return next;
		}
		return null;
	}
}

/**
 *
 * Default PermissionCollection implementation for Permission classes that
 * return null for {@link Permission#newPermissionCollection()}.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
final class DefaultPermissionCollection extends PermissionCollection {

	private Vector<Permission> _perms;

	public DefaultPermissionCollection() {
		_perms = new Vector<Permission>();
	}

	public synchronized void add(Permission permission) {
		_perms.addElement(permission);
	}

	public synchronized boolean implies(Permission permission) {

		Permission p;

		if (_perms.indexOf(permission, 0) != -1) {
			Enumeration<Permission> enum_ = _perms.elements();
			while (enum_.hasMoreElements()) {
				p = enum_.nextElement();
				if (p.implies(permission))
					return true;
			}
			return false;
		} else {
			return true;
		}
	}

	public synchronized Enumeration<Permission> elements() {
		return _perms.elements();
	}

}

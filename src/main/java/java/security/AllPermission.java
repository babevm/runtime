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
 * A Permission class that implies <i>all</i> other permissions.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public final class AllPermission extends Permission {

	/**
	 * Creates a new AllPermission.
	 */
	public AllPermission() {
		super("<all permissions>");
	}

	/**
	 * Creates a new AllPermission. The name and actions are ignored.
	 */
	public AllPermission(String name, int actions) {
		this();
	}

	/**
	 * Always returns <code>true</code>. An AllPermission implies all other
	 * permissions.
	 *
	 * @see java.security.Permission#implies(java.security.Permission)
	 */
	public boolean implies(Permission p) {
		return true;
	}

	public boolean equals(Object obj) {
		return (obj instanceof AllPermission);
	}

	public PermissionCollection newPermissionCollection() {
		return new AllPermissionCollection();
	}

}

/**
 *
 * A <code>PermissionCollection</code> class for <code>AllPermission</code>
 * objects. Will imply any permission as long as a single
 * <code>AllPermission</code> object has been added. Otherwise, will imply none.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
final class AllPermissionCollection extends PermissionCollection {

	boolean _hasAllPermissions;

	public AllPermissionCollection() {
		_hasAllPermissions = false;
	}

	public void add(Permission permission) {
		if (!(permission instanceof AllPermission))
			throw new IllegalArgumentException("invalid permission: " + permission);

		if (_readOnly)
			throw new SecurityException("readonly PermissionCollection");

		_hasAllPermissions = true;
	}

	public boolean implies(Permission permission) {
		return _hasAllPermissions;
	}

	public Enumeration<Permission> elements() {

		return new Enumeration<Permission>() {

			private boolean _hasMore = _hasAllPermissions;

			public boolean hasMoreElements() {
				return _hasMore;
			}

			public Permission nextElement() {
				_hasMore = false;
				return SecurityManager.ALL_PERMISSION;
			}

		};
	}
}
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
 * <p>
 * A combination of code source and 'static' permissions that identifies running
 * code.
 *
 * <p>
 * During access permission checking all protection domains in the current
 * execution context have their {@link #implies(Permission)} method called.
 *
 * <p>
 * Generally, a ProtectionDomain delegates the implies() to the system security
 * policy. However, at construction time, a PermissionCollection of 'static'
 * permission may be associated with a ProtectionDomain. 'Static' permissions
 * are permission that are independent of any system security policy.
 *
 * <p>
 * If a ProtectionDomain has static permission, they are first checked for
 * implication before delegation to the system security policy.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public final class ProtectionDomain {

	private CodeSource _codeSource;
	private PermissionCollection _permissions;

	private boolean _hasAllPermissions;

	/**
	 * <p>
	 * Creates a new ProtectionDomain object for the given CodeSource and
	 * Permissions.
	 *
	 * <p>
	 * If the given permissions object is not <code>null</code> it will be set
	 * as read-only.
	 *
	 * <p>
	 * Calling code must have the "createProtectionDomain"
	 * {@link RuntimePermission}.
	 *
	 * @param codeSource a given CodeSource
	 * @param permissions a given Permissions
	 */
	public ProtectionDomain(CodeSource codeSource, PermissionCollection permissions) {

		SecurityManager.checkPermission(SecurityManager.CREATE_PD_PERMISSION);

		_codeSource = codeSource;
		_permissions = permissions;

		if (_permissions != null) {

			/* make the permission collection immutable */
			_permissions.setReadOnly();

			/*
			 * check if given permissions is an AllPermissionCollection or a
			 * Permissions with an AllPermission - these are the only two
			 * circumstances that a PermissionCollection will imply another
			 * permission without checking actually the system security Policy.
			 */
			if (_permissions instanceof Permissions) {
				_hasAllPermissions = ((Permissions) _permissions)._hasAllPermissions;
			} else if (_permissions instanceof AllPermissionCollection) {
				_hasAllPermissions = ((AllPermissionCollection) _permissions)._hasAllPermissions;
			}
		}
	}

	/**
	 * Returns the CodeSource.
	 *
	 * @return the CodeSource
	 */
	public CodeSource getCodeSource() {
		return _codeSource;
	}

	/**
	 * Returns the static permissions. Note that the returned
	 * PermissionCollection object, if not <code>null</code>, will be read-only.
	 *
	 * @return the permissions.
	 */
	public PermissionCollection getPermissions() {
		return _permissions;
	}

	/**
	 * Reports if this ProtectionDomain implies a given permission. If a static
	 * PermissionCollection object was provided at construction it is first
	 * checked. If that does not fail the system security policy is then
	 * checked.
	 *
	 * @param permission a given permission to check.
	 * @return <code>true</code> if the given permission is implied by the
	 *         ProtectionDomain, <code>false</code> otherwise.
	 */
	public boolean implies(Permission permission) {

		if (_hasAllPermissions)
			return true;

		if (_permissions != null) {

			/*
			 * check the permissions for a failure - at this point we know the
			 * permission does not have an AllPermission object - that is check
			 * in the constructor and catered for in the above
			 * _hasAllPermissions check.
			 *
			 * If the permission implies fails, we do not need to check the
			 * system security policy.
			 */
			if (!_permissions.implies(permission))
				return false;
		}

		/* finally, delegate to the system security policy */
		return Policy.implies(this, permission);
	}
}

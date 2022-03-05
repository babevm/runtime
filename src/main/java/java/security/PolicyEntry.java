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
 * An immutable collection of permissions for a {@link CodeSource}. PolicyEntry
 * objects are the basic element of system security policy. Typically, they are
 * only created by a {@link PolicyLoader} implementation when requested by the
 * {@link Policy} object.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public final class PolicyEntry {

	CodeSource _codeSource;
	Permissions _permissions;

	/**
	 * Creates a new PolicyEntry object. The given permission is marked as
	 * read-only on creation of this policy - it will also become immutable.
	 *
	 * @param codeSource
	 * @param permissions
	 */
	public PolicyEntry(CodeSource codeSource, Permissions permissions) {
		_codeSource = codeSource;
		if (permissions != null) {
			permissions.setReadOnly();
			_permissions = permissions;
		}
	}

	/**
	 * Returns the {@link CodeSource} for this object;
	 *
	 * @return the CodeSource.
	 */
	public CodeSource getCodeSource() {
		return _codeSource;
	}

	/**
	 * Returns the {@link Permissions} for this object.
	 *
	 * @return the permissions.
	 */
	public Permissions getPermissions() {
		return _permissions;
	}

}

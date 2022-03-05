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
 * Thrown when an access control permission check fails.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public final class AccessControlException extends SecurityException {

	private Permission _permission;

	public AccessControlException(String s) {
		super(s);
	}

	public AccessControlException(String s, Permission p) {
		super(s);
		_permission = p;
	}

	public Permission getPermission() {
		return _permission;
	}
}

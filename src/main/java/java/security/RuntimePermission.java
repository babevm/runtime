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
 * For permissions of the Babe runtime classes. Developers should not use this
 * permission class for their own permissions.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public final class RuntimePermission extends BasicPermission {

	/**
	 * Create a new RuntimePermission object of the given name with no actions.
	 *
	 * @param name the (mandatory) permission name.
	 */
	public RuntimePermission(String name) {
		super(name);
	}

	/**
	 * Create a new RuntimePermission object of the given name with the given actions.
	 *
	 * @param name the (mandatory) permission name.
	 * @param actions the (optional) actions.
	 */
	public RuntimePermission(String name, int actions) {
		super(name, actions);
	}

}

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

package babe.security;

import java.security.PolicyEntry;
import java.security.PolicyLoader;
import java.security.SecurityManager;
import java.security.SecurityPermission;

/**
 * A PolicyLoader implementation that provides all permissions to all code.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public final class AllPermissionsPolicyLoader implements PolicyLoader {

	/**
	 * Creates a new AllPermissionsPolicyLoader object. Code calling this
	 * method must have the "createPolicyLoader" SecurityPermission.
	 */
	public AllPermissionsPolicyLoader() {
		SecurityManager.checkPermission(new SecurityPermission("createPolicyLoader"));
	}

	public PolicyEntry[] getPolicyEntries() {
		SecurityManager.checkPermission(new SecurityPermission("getPolicyEntries"));
		return new PolicyEntry[] { new PolicyEntry(null, SecurityManager.ALL_PERMISSIONS) };
	}

}

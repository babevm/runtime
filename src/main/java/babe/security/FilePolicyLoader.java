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

import java.security.Permission;
import java.security.Permissions;
import java.security.PolicyEntry;
import java.security.PolicyLoader;
import java.security.SecurityManager;
import java.security.SecurityPermission;
import java.util.PropertyPermission;
import java.util.Vector;

/**
 * TODO: A PolicyLoader implementation that reads the security policy from a file.  This does not yet
 * read / parse that file format.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public final class FilePolicyLoader implements PolicyLoader {

	/**
	 * Creates a new FilePolicyLoader object. Code calling this
	 * method must have the "createPolicyLoader" SecurityPermission.
	 */
	public FilePolicyLoader() {
		SecurityManager.checkPermission(new SecurityPermission("createPolicyLoader"));
	}

	public PolicyEntry[] getPolicyEntries() {
		SecurityManager.checkPermission(new SecurityPermission("getPolicyEntries"));

		Vector<PolicyEntry> v = new Vector<PolicyEntry>();
		Permissions perms = new Permissions();

		perms.add(new PropertyPermission("java.os", Permission.ACTION_READ));
		perms.add(new PropertyPermission("java.os", Permission.ACTION_WRITE));

		v.addElement(new PolicyEntry(null, perms));

		PolicyEntry pe[] = new PolicyEntry[v.size()];
		v.copyInto(pe);
		return pe;
	}

}

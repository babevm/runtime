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

import java.util.Hashtable;

import babe.security.AllPermissionsPolicyLoader;
import babe.security.FilePolicyLoader;

/**
 * <p>
 * A system-wide installed security policy. The Policy singleton role in the
 * security architecture is to maintain a mapping of permissions for codesources
 * - this in effect is the system security 'policy'. Permissions for a given
 * codesource are represented by a {@link PolicyEntry} object. Note that the
 * class is 'package' private - it is not available at all to classes outside of
 * this package.
 *
 * <p>
 * The Policy class has the responsibility of initialising the system security
 * policy and to check permissions of {@link ProtectionDomain} objects against
 * that policy.
 *
 * <p>
 * Unlike the J2SE security implementation, which enables the replacement of a
 * system wide policy object, this implementation has all methods on the Policy
 * class as static.
 *
 * <p>
 * The actual loading of a security policy is delegated to a
 * {@link PolicyLoader} implementation. A policy loader has a single
 * responsibility which is to provide this Policy with all the
 * {@link PolicyEntry} objects that define the security policy.
 *
 * <p>
 * A PolicyLoader is called upon in two circumstances. Firstly, it is used to
 * initialise the security policy when the Policy object is initialised.
 * Secondly, it will be called when the {@link Policy#refresh()} method is
 * called. Refreshing the security policy clears the currently held policy
 * (defined by the held set of PolicyEntry objects) and re-requests the policy
 * loader to provide the security policy. This enable complete reloading of a
 * system security policy at runtime. Just the required thing, for example, if
 * applications are dynamically downloaded and the security policy is updated
 * accordingly.
 *
 * <p>
 * The default policy loader is the {@link FilePolicyLoader}. This loader reads
 * a file to create a security policy. Refer to the documentation on that class
 * for further information on the format of that file.
 *
 * <p>
 * The default policy loader may be set by the "java.security.policyloader"
 * system property. It may be set on the command line as
 * "-Djava.security.policyloader=xxxx" where xxxx is the fully qualified name
 * name of the PolicyLoader implementation class.
 *
 * <p>
 * The policy loader implementation class must be visible to the bootstrap
 * classloader and therefore will need to be included in the classpath specified
 * using the "-Xbootclasspath" VM command line argument.
 *
 * <p>
 * Failure to find or load the specified policy loader class will result in a
 * blanket "no permissions" security policy. That is, no code will receive any
 * permissions at all. This will likely cause application execution VM to fail
 * very quickly.
 *
 * <p>
 * Note that a {@link ProtectionDomain} object's permission are not held with
 * the protection domain. Each time a protection domain implies() is called for
 * a permission , the protection domain checks with the policy whether the
 * permission is implied for it. This separation of protection domain from its
 * permission facilitates the dynamic refresh of security policy as described
 * above.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
final class Policy {

	/* array of policy entries created by policy entry provider */
	private static PolicyEntry[] _policyEntries;

	/* static policy entry allow all permissions */
	private static PolicyEntry[] _ALLPERMS_POLICYENTRY;

	/* cache of protection domains and their permissions - cleared on refresh() */
	private static Hashtable<ProtectionDomain, Permissions> _pdCache = new Hashtable<ProtectionDomain, Permissions>();

	static {
		/*
		 * create a static array of PolicyEntry with a single element that
		 * allows all permissions
		 */
		_ALLPERMS_POLICYENTRY = new PolicyEntry[] { new PolicyEntry(null, SecurityManager.ALL_PERMISSIONS) };
	}

	/**
	 * Private constructor - enforces singleton
	 */
	private Policy() {
	}

	/**
	 * Determines whether a given {@link Permission} is implied by the system
	 * security policy for a given {@link ProtectionDomain}.
	 *
	 * @param domain a given ProtectionDomain
	 * @param permission a given Permission
	 *
	 * @return <code>true</code> if the permission is implied,
	 *         <code>false</code> otherwise.
	 */
	public static boolean implies(ProtectionDomain domain, Permission permission) {

		/*
		 * If there are no PolicyEntry objects registered, we'll need to
		 * initialise this policy object. _entries is null on startup, and also
		 * after a refresh - note that a refresh does not actually get the
		 * policies immediately - it delays that process until the first
		 * 'implies' is called after a refresh.
		 */

		if (_policyEntries == null) {

			/*
			 * Before initialising the policy entries, we'll assign a single
			 * 'all permissions' entry to the entries list to be during
			 * initialisation. This will make sure that any 'implies' calls made
			 * during initialisation are given permission and do not result in a
			 * nasty infinite loop.
			 */

			_policyEntries = _ALLPERMS_POLICYENTRY;

			final PolicyLoader policyLoader;

			String classname = System.getProperty("java.security.policyloader");

			if (classname != null) {
				try {
					policyLoader = (PolicyLoader) Class.forName(classname).newInstance();
				} catch (Throwable e) {
					throw new SecurityException("Unable to instantiate PolicyLoader: " + classname).initCause(e);
				}
			} else {
				// policyLoader = new FilePolicyLoader();
				policyLoader = new AllPermissionsPolicyLoader();
			}

			/*
			 * use the PolicyLoader to create the system security policy.
			 */
			_policyEntries = SecurityManager.doPrivileged(new PrivilegedAction<PolicyEntry[]>() {

				public PolicyEntry[] run() {
					return policyLoader.getPolicyEntries();
				}

			});

			/* if the policy loader has failed */
			if (_policyEntries == null)
				throw new SecurityException("Policy loader failed to provide policy entries");

			/*
			 * after loading, just in case any protection domains were given
			 * ALLPERM access during loading, we'll clean the cache just to be
			 * sure.
			 */
			_pdCache.clear();
		}

		/* check the domain cache for already-determined permissions */
		Permissions pdPerms = _pdCache.get(domain);

		/*
		 * if no permissions where cached for the domain, determine them now,
		 * and then cache them
		 */
		if (pdPerms == null) {
			pdPerms = getDomainPermissions(domain);
			_pdCache.put(domain, pdPerms);
		}

		/*
		 * and finally, 'implies' is delegated to the domain permissions
		 */
		return pdPerms.implies(permission);
	}

	/**
	 * Determine the permissions for a given ProtectionDomain. If the CodeSource
	 * of the given protection domain is <code>null</code> it will get all
	 * permissions. Otherwise, all {@link PolicyEntry} object returned from the
	 * {@link PolicyLoader} are inspected for matching a <code>CodeSource</code>
	 * . Permissions from PolicEntry objects with a matching
	 * <code>CodeSource</code> are added to the domain's permissions.
	 *
	 * @param domain a given domain
	 *
	 * @return the permissions for the given domain
	 */
	private static Permissions getDomainPermissions(ProtectionDomain domain) {

		CodeSource cs = domain.getCodeSource();

		/*
		 * if the domain CodeSource is null, give it all permissions. A null
		 * code source implies system code.
		 */
		if (cs == null)
			return SecurityManager.ALL_PERMISSIONS;

		Permissions perms = new Permissions();

		/*
		 * otherwise, check all the policy entries for a CodeSource match and
		 * add the permissions of each entry with a matching CodeSource to the
		 * domain permissions
		 */
		for (int i = 0; i < _policyEntries.length; i++) {

			CodeSource ecs = _policyEntries[i]._codeSource;

			if ((ecs == null) || (ecs.equals(cs))) {
				perms.add(_policyEntries[i]._permissions);
			}
		}

		return perms;

	}

	/**
	 * Refresh the {@link PolicyEntry} objects held by this Policy. PolicyEntry
	 * object are provided by a {@link PolicyLoader} implementation. Note that
	 * any reload of the policy entry objects using the policy loader is lazy -
	 * it does not happen until the next time an
	 * {@link #implies(ProtectionDomain, Permission)} is requested.
	 *
	 * Code calling this method must have the "refreshPolicy"
	 * SecurityPermission.
	 */
	public static void refresh() {
		SecurityManager.checkPermission(new SecurityPermission("refreshPolicy"));
		_policyEntries = null;
		_pdCache.clear();
	}

}

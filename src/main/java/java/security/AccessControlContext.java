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

import java.util.Vector;

/**
 * <p>An execution context as represented by the protection domains present on the
 * java execution stack at a given time.
 *
 * <p>An AccessControlContext object is used by the permissions logic to determine
 * who is executing the code on the java stack.
 *
 * @author Greg McCreath
 * @since 0.0.1
 */
public final class AccessControlContext {

	private ProtectionDomain _context[];

	/**
	 * Create a new AccessControlContext for a given set of protection domains.
	 *
	 * @param context a given set of protection domains.
	 */
	AccessControlContext(ProtectionDomain context[]) {

		AccessControlContext inheritedContext = SecurityManager.getInheritedAccessControlContext();

		if (context != null) {
			if (context.length == 0) {
				_context = null;
			} else if (context.length == 1) {
				if (context[0] != null) {
					_context = (ProtectionDomain[]) context.clone();
				} else {
					_context = null;
				}
			}

			/*
			 * merge the current execution context with the inherited one (if
			 * any). This gives us a complete context snapshot.
			 */
			mergeWith(inheritedContext);

		} else {
			/* no domains? then just use the inherited one (if any) */
			if (inheritedContext != null)
				_context = inheritedContext._context;
		}
	}

	/**
	 * Check a given permission against this context. Note that the permission is
	 * also checked against any inherited context that the current thread may
	 * have.
	 *
	 * @param permission a given permission - may not be <code>null</code>.
	 * @throws AccessControlException if permission is not granted.
	 */
	public void checkPermission(Permission permission) throws AccessControlException {

		if (permission == null) {
			throw new NullPointerException("null permission");
		}

		/* if null, then only system code is on the stack */
		if (_context == null)
			return;

		/*
		 * for each domain in the context, check that it implies the given
		 * permission
		 */
		for (int i = 0; i < _context.length; i++) {
			if (_context[i] != null && !_context[i].implies(permission)) {
				throw new AccessControlException("access denied " + permission, permission);
			}
		}

		/*
		 * check the inherited context if any. The inherited context is stored
		 * on the current thread - but it is gained natively so as to avoid
		 * exposing the method on Thread.
		 */
		AccessControlContext inheritedContext = SecurityManager.getInheritedAccessControlContext();

		if (inheritedContext != null) {
			ProtectionDomain c[] = inheritedContext._context;
			for (int i = 0; i < c.length; i++) {
				if (c[i] != null && !c[i].implies(permission)) {
					throw new AccessControlException("access denied " + permission, permission);
				}
			}
		}
	}

	/**
	 * Merges this context with another given context - the end result being the
	 * current context is updated with the protection domains contained in the
	 * other context that are not contained in this one.
	 *
	 * @param otherContext a given context to merge into this one.
	 * @return this context.
	 */
	AccessControlContext mergeWith(AccessControlContext otherContext) {

		/*
		 * if there is no context to merge with, or the context actually has no
		 * domains in it, there is no point continuing.
		 */
		if ((otherContext == null) || (otherContext._context == null))
			return this;

		/* merge the two context's protection domains */

		Vector<ProtectionDomain> v = new Vector<ProtectionDomain>();

		ProtectionDomain domains[] = _context;

		/* add the domains from this context */
		for (int i = 0; i < domains.length; i++) {
			ProtectionDomain d = domains[i];
			if (d != null)
				v.addElement(d);
		}

		/* add the domains from the inherited context */
		domains = otherContext._context;
		for (int i = 0; i < domains.length; i++) {
			ProtectionDomain d = domains[i];
			if (d != null && !v.contains(d))
				v.addElement(d);
		}

		/* copy the contents into a new array */
		_context = new ProtectionDomain[v.size()];
		v.copyInto(_context);

		return this;
	}
}

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
 * The hub of access control permission checking. The SecurityManager can be
 * used to check permissions, execute code in a privileged manner, and to get a
 * snapshot of the current execution context ({@link AccessControlContext}).
 *
 * <p>
 * The SecurityManager also defines a number of pre-built permission objects for
 * common usage.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */

// some readin:

	// https://dl.acm.org/doi/pdf/10.5555/1698154
	// https://docs.oracle.com/javase/1.5.0/docs/guide/security/spec/security-spec.doc3.html

public final class SecurityManager {

	/** An {@link AllPermission} instance */
	public static final AllPermission ALL_PERMISSION = new AllPermission();

	/** A {@link RuntimePermission} permission "createClassLoader" */
	public static final RuntimePermission CREATE_CLASSLOADER_PERMISSION = new RuntimePermission("createClassLoader");

	/** A {@link RuntimePermission} permission "modifyThread" */
	public static final RuntimePermission MODIFY_THREAD_PERMISSION = new RuntimePermission("modifyThread");

	/** A {@link RuntimePermission} permission "createThread" */
	public static final RuntimePermission CREATE_THREAD_PERMISSION = new RuntimePermission("createThread");

	/** A {@link RuntimePermission} permission "getThread" */
	public static final RuntimePermission GET_THREAD_PERMISSION = new RuntimePermission("getThread");

	/** A {@link RuntimePermission} permission "getProtectionDomain" */
	public static final RuntimePermission GET_PD_PERMISSION = new RuntimePermission("getProtectionDomain");

	/** A {@link RuntimePermission} permission "getClassLoader" */
	public static final RuntimePermission GET_CLASSLOADER_PERMISSION = new RuntimePermission("getClassLoader");

	/** A {@link RuntimePermission} permission "createProtectionDomain" */
	public static final RuntimePermission CREATE_PD_PERMISSION = new RuntimePermission("createProtectionDomain");

	/** A Permissions instance with all permissions */
	public static final Permissions ALL_PERMISSIONS;

	/** A ProtectionDomain with static all permissions */
	public static final ProtectionDomain ALL_PERMISSION_DOMAIN;

	static {
		/* create a ProtectionDomain with all permissions */
		ALL_PERMISSIONS = new Permissions();
		ALL_PERMISSIONS.add(ALL_PERMISSION);
		ALL_PERMISSIONS.setReadOnly(); /* seal it */

		/* create a domain with all permissions */
		ALL_PERMISSION_DOMAIN = new ProtectionDomain(null, ALL_PERMISSIONS);
	}

	/**
	 * Private Constructor.
	 */
	private SecurityManager() {
	}

	/**
	 * <p>
	 * Check a given permission against the the current execution context. An
	 * exception is thrown if the permission check fails. Returns silently if
	 * the permissions check passes.
	 *
	 * <p>
	 * Note that execution context represents all protections domains on the
	 * java execution stack entire - or as far down as the first 'privileged'
	 * operation if there is one.
	 *
	 * @param permission a given permission - may not be <code>null</code>.
	 * @throws AccessControlException if the permissions check fails.
	 */
	public static void checkPermission(Permission permission) throws AccessControlException {

		AccessControlContext context = getStackAccessControlContext();

		/*
		 * if context is null, we had all system code on the stack, or above a
		 * privileged operation.
		 */
		if (context != null)
			context.checkPermission(permission);
	}

	/**
	 * <p>
	 * Returns the current execution context as represented by an
	 * {@link AccessControlContext}. The context represents the protection
	 * domains that exist on the java stack when this method is called.
	 *
	 * <p>
	 * Note that the (native) algorithm for locating the protection domains
	 * terminates when it encounters a privileged operation. That is, the
	 * context returned here may not be for the full java stack, it may
	 * represent a portion of the java stack above (and including) a privileged
	 * operation.
	 *
	 * @return an AccessControlContext, or <code>null</code> if all the code, or
	 *         privileged code on the stack is system code.
	 */
	public static AccessControlContext getContext() {
		AccessControlContext acc = getStackAccessControlContext();
		return (acc == null) ? new AccessControlContext(null) : acc.mergeWith(getInheritedAccessControlContext());
	}

	/**
	 * <p>
	 * Returns the AccessControl context. i.e., it gets the protection domains
	 * of all the relevant callers on the java execution stack, starting at the
	 * first class with a non- <code>null</code> ProtectionDomain.
	 *
	 * @return the access control context based on the current stack or
	 *         <code>null</code> if there was only privileged system code.
	 */

	private static native AccessControlContext getStackAccessControlContext();

	/**
	 * <p>
	 * Executes code as 'privileged'. In access control terms, privileged code
	 * marks a point in the stack where protection domain below it are ignored.
	 *
	 * <p>
	 * Effectively, it say to run some code and consider only protection domains
	 * above it for access control.
	 *
	 * <p>
	 * A privileged operation is one that runs trusted code on behalf of
	 * potentially untrusted code - like allowing system code to read/write to
	 * a, say, passwords file on behalf on code that does not have permissions
	 * to do so.
	 *
	 * <p>
	 * Privileged code is run by supplying an instance of
	 * {@link PrivilegedAction}. Often, this would be an anonymous inner class.
	 *
	 * <p>
	 * Note that <code>PrivilegedAction</code> is generic, this gives a type to
	 * the return value of its <code>run()</code> method.
	 *
	 * @param <T> the return type of the {@link PrivilegedAction#run()} method.
	 * @param action the privileged code to run. Must not be <code>null</code>,
	 *            but no null checking is performed.
	 * @return a value of type <T> or null.
	 */
	public static <T> T doPrivileged(PrivilegedAction<T> action) {
		return action.run();
	}

	/**
	 * <p>
	 * Execute code as privileged - where that code may throw an exception.
	 *
	 * <p>
	 * Refer notes on {@link #doPrivileged(PrivilegedAction)} regarding
	 * privileged operations.
	 *
	 * @param <T> the return type of the {@link PrivilegedAction#run()} method.
	 * @param action the privileged code to run. Must not be <code>null</code>,
	 *            but no null checking is performed.
	 * @return a value of type <T> or null.
	 * @throws PrivilegedActionException if any <code>Exception</code> is thrown
	 *             in the privileged code.
	 */
	public static <T> T doPrivileged(PrivilegedExceptionAction<T> action) throws PrivilegedActionException {
		try {
			return action.run();
		} catch (Exception e) {
			throw new PrivilegedActionException(e);
		}
	}

	/**
	 * <p>
	 * Returns the current thread's inherited access control context, or
	 * <code>null</code> if it has none.
	 *
	 * <p>
	 * At the moment of new thread creation, a snapshot of the access control
	 * context of a new thread's parent is taken. All subsequent access control
	 * decisions for the new child thread take into account that inherited
	 * context.
	 *
	 * <p>
	 * A thread's inherited context does not change after the snapshot - it
	 * remains the same for the lifetime of the thread.
	 *
	 * @return an AccessControlContext
	 */
	static native AccessControlContext getInheritedAccessControlContext();

}

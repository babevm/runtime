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

package java.lang;

import java.security.ProtectionDomain;
import java.security.SecurityManager;

/**
 *
 * Instances of the class Class represent classes and interfaces in a running
 * application.
 *
 * Class has no public constructor. Instead, Class objects are constructed
 * automatically by the Virtual Machine as classes are loaded.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 * @param <T>
 */
public final class Class<T> {

	// **********************************************************
	// VM assumes these attributes. Do not insert any attributes above
	// this.
	private Object vm_clazz;	/* the internal VM structure for this class */
	private Thread init_thread;	/* handle to initialising threading during init */
	//private ProtectionDomain _protectionDomain;	/* handle to initialising threading during init */
	// **********************************************************

	static final String BOOLEAN_CLASS = "boolean";
	static final String BYTE_CLASS = "byte";
	static final String LONG_CLASS = "long";
	static final String CHARACTER_CLASS = "char";
	static final String FLOAT_CLASS = "float";
	static final String DOUBLE_CLASS = "double";
	static final String INTEGER_CLASS = "int";
	static final String SHORT_CLASS = "short";

	/* loading and init states of the VM clazz */
	private static final int CLAZZ_STATE_ERROR  		= -1;
	private static final int CLAZZ_STATE_LOADING        = 0;
	private static final int CLAZZ_STATE_LOADED   		= 1;
	private static final int CLAZZ_STATE_INITIALISING   = 2;
	private static final int CLAZZ_STATE_INITIALISED    = 3;

	// hide the constructor - effectively makes this class abstract. Only the VM
	// will be creating instances of this class.
	private Class() {
	}

	public native static Class<?> forName(String name) throws ClassNotFoundException;

	public static Class<?> forName(String name, boolean initialize,
            ClassLoader loader) throws ClassNotFoundException {
		return forName2(name, initialize, loader);
	}

	private native static Class<?> forName2(String name, boolean initialize,
            ClassLoader loader) throws ClassNotFoundException;

	public native T newInstance() throws IllegalAccessException,
			InstantiationException;

	public native String getName();

	/**
	 * Note carefully - this maps onto the same native function as forName().
	 * Note that no exceptions are thrown - the VM creates these classes - they do
	 * not need to have a source file
	 */
	static native Class<?> getPrimitiveClass(String type);

	public native Class<? super T> getSuperclass();

	public native boolean isInterface();

	public String toString() {
		return (isInterface() ? "interface " : "class ") + this.getName();
	}

	/* getter and setter for the VM to change the state of its internal class structure */
	private native int getState();
	private native void setState(int newState);

	/* gets the current thread from the VM without asking the Thread class.  Why?
	 * Because we need to get currentThread for this process, but this process is called
	 * when we try to get Thread.currentThread()  (it is static).  So, we have a private
	 * back door here to get the thread without using the Thread class.
	 */
	private native Thread getInitThread();

	/* __doclinit causes the VM to place the <clinit> method on the stack for execution */
	private native void __doclinit();

	/**
	 * Implements the "2.17.5 Detailed Initialization Procedure" of the JVMS.  Unlike other VMs I have
	 * looked at which do all the class init in 'C' I have chosen to do as much as possible in
	 * Java.  The threading and locking is so much easier.  However, a few support methods have been
	 * added to the VM to allow this to happen.  Eg, it is not possible to get through this init
	 * by making a call to Thread.currentThread().  That method call is static and will cause a spin on
	 * this code - so a special method called getInitThread is called which bypasses the Thread
	 * class.
	 *
	 * Also, a bit of magic happens with the <clinit> method.  The __doclinit method is a native
	 * method that pushes the <clinit> method onto the stack.
	 *
	 * No doubt, doing this in Java code is going to be slower than the VM, but I gotta tell you.
	 * It is soooo much cleaner and simpler having it here.  Trying to do threading and wait() and so on
	 * within the VM would be difficult.
	 *
	 * The numbered notes in this method are copied from the JVMS to say exactly what is required at
	 * each step.
	 *
	 * @throws Throwable
	 */
	private void __doInit() throws Throwable {

		/*
		 * 1. Synchronize on the Class object that represents the class or
		 * interface to be initialised. This involves waiting until the current
		 * thread can obtain the lock for that object
		 */
		synchronized (this) {

			int state = getState();

			Thread currentThread = getInitThread();

			/*
			 * 2. If initialization by some other thread is in progress for the
			 * class or interface, then wait on this Class object (which
			 * temporarily releases the lock). When the current thread awakens
			 * from the wait, repeat this step
			 */
			try {
				while ((state == CLAZZ_STATE_INITIALISING)
						&& (init_thread != currentThread)) {
					this.wait();
					state = getState();
				}
			} catch (InterruptedException e) {
				// TODO What ?? Docs do not say, but this seems reasonable ...
				setState(CLAZZ_STATE_ERROR);
			}

			/*
			 * 3. If initialization is in progress for the class or interface by
			 * the current thread, then this must be a recursive request for
			 * initialization. Release the lock on the Class object and complete
			 * normally.
			 */
			if ((state == CLAZZ_STATE_INITIALISING) && (init_thread == currentThread))
				return;

			/*
			 * 4. If the class or interface has already been initialized, then
			 * no further action is required. Release the lock on the Class
			 * object and complete normally.
			 */
			if (state == CLAZZ_STATE_INITIALISED)
				return;

			/*
			 * 5. If the Class object is in an erroneous state, then
			 * initialization is not possible. Release the lock on the Class
			 * object and throw a NoClassDefFoundError.
			 */
			if (state == CLAZZ_STATE_ERROR)
				throw new NoClassDefFoundError();

			/*
			 * 6. Otherwise, record the fact that initialization of the Class
			 * object is now in progress by the current thread and release the
			 * lock on the Class object.
			 */
			setState(CLAZZ_STATE_INITIALISING);
			init_thread = currentThread;

			/* exit the sync block to release the lock for step 6. */
		}

		/*
		 * 7. Next, if the Class object represents a class rather than an
		 * interface, and the direct superclass of this class has not yet been
		 * initialized, then recursively perform this entire procedure for the
		 * uninitialized superclass. If the initialization of the direct
		 * superclass completes abruptly because of a thrown exception, then
		 * lock this Class object, label it erroneous, notify all waiting
		 * threads, release the lock, and complete abruptly, throwing the same
		 * exception that resulted from the initializing the superclass.
		 */

		if (!isInterface()) {

			Class<?> sc = getSuperclass();

			if ((sc != null) && (sc.getState() <= CLAZZ_STATE_LOADED)) {
				try {
					sc.__doInit();
				} catch (Throwable t) {

					synchronized (this) {
						setState(CLAZZ_STATE_ERROR);
						init_thread = null;
						notifyAll();
					}

					throw t;
				}
			}
		}

		/*
		 * 8. Next, execute either the class variable initializers and static
		 * initializers of the class or the field initializers of the interface,
		 * in textual order, as though they were a single block, except that
		 * final static variables and fields of interfaces whose values are
		 * compile-time constants are initialized first.
		 */

		try {

			/* execute the <clinit> method by using the native helper function. */
			__doclinit();

			/*
			 * 9. If the execution of the initializers completes normally, then
			 * lock this Class object, label it fully initialized, notify all
			 * waiting threads, release the lock, and complete this procedure
			 * normally.
			 */
			synchronized (this) {
				setState(CLAZZ_STATE_INITIALISED);
				init_thread = null;
				notifyAll();
			}

			return;

		} catch (Throwable t) {

			/*
			 * 10. Otherwise, the initialisers must have completed abruptly by
			 * throwing some exception E. If the class of E is not Error or one
			 * of its subclasses, then create a new instance of the class
			 * ExceptionInInitializerError, with E as the argument, and use this
			 * object in place of E in the following step. But if a new instance
			 * of ExceptionInInitializerError cannot be created because an
			 * OutOfMemoryError occurs, then instead use an OutOfMemoryError
			 * object in place of E in the following step.
			 */

			Throwable tt = t;

			if (!(t instanceof Error)) {
				try {
					tt = new ExceptionInInitializerError();
				} catch (OutOfMemoryError e) {
					tt = e;
				}
				tt.initCause(t);
			}

			/*
			 * 11. Lock the Class object, label it erroneous, notify all waiting
			 * threads, release the lock, and complete this procedure abruptly
			 * with reason E or its replacement as determined in the previous
			 * step.
			 */
			synchronized (this) {
				setState(CLAZZ_STATE_ERROR);
				init_thread  = null;
				notifyAll();
			}

			throw tt;
		}

	}

    public native boolean desiredAssertionStatus();

    public ClassLoader getClassLoader() {
    	SecurityManager.checkPermission(SecurityManager.GET_CLASSLOADER_PERMISSION);
    	return getClassLoader0();
    }

    private native ClassLoader getClassLoader0();

    public java.security.ProtectionDomain getProtectionDomain() {
        SecurityManager.checkPermission(SecurityManager.GET_PD_PERMISSION);
        ClassLoader cl = getClassLoader();
        ProtectionDomain pd = (cl == null) ? null : cl.getProtectionDomain();
        return (pd != null) ? pd : SecurityManager.ALL_PERMISSION_DOMAIN;
    }
}

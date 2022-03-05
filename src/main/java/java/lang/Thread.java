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

import java.security.AccessControlContext;
import java.security.RuntimePermission;
import java.security.SecurityManager;

/**
 * @author Greg McCreath
 * @since 0.0.1
 */
public class Thread implements Runnable {

	public static final int MIN_PRIORITY = 1;
	public static final int NORM_PRIORITY = 5;
	public static final int MAX_PRIORITY = 10;

	public static final int STATE_NEW = 1;
	public static final int STATE_RUNNABLE = 2;
	public static final int STATE_BLOCKED = 4;
	public static final int STATE_WAITING = 12; /* is 0x8 or'd with BLOCKED */
	public static final int STATE_TIMED_WAITING = 20; /* is 0x10 or'd with BLOCKED */
	public static final int STATE_TERMINATED = 32;

	/**
	 * fields are explicitly used by the VM ... DO NOT move or alter or
	 * rearrange *
	 */
	private Runnable target;
	private String name;
	private Object vm_thread;
	private int id;
	private int priority;
	private boolean daemon = false;
	private AccessControlContext _inheritedContext;
	/** ******************* */

	// null unless explicitly set
	private UncaughtExceptionHandler _uncaughtExceptionHandler;

	// null unless explicitly set
	private static UncaughtExceptionHandler _defaultUncaughtExceptionHandler;

	/**
	 * Returns the next thread id - as calculated by the VM.
	 *
	 * @return the next thread id.
	 */
	private native static int nextThreadId();

	/**
	 * <p>
	 * Creates a new Thread.
	 *
	 * <p>
	 * Calling code must have the "createThread" {@link RuntimePermission}.
	 */
	public Thread() {
		SecurityManager.checkPermission(SecurityManager.CREATE_THREAD_PERMISSION);
		id = nextThreadId();
		setPriority0(currentThread().getPriority());
		setDaemon(currentThread().isDaemon());
		name = "Thread-" + id;
		_inheritedContext = SecurityManager.getContext();
	}

	/**
	 * <p>
	 * Creates a new Thread to execute a given {@link Runnable}.
	 *
	 * <p>
	 * Calling code must have the "createThread" {@link RuntimePermission}.
	 */
	public Thread(Runnable target) {
		this();
		this.target = target;
	}

	public void run() {
		if (target != null)
			target.run();
	}

	/**
	 * <p>
	 * Causes this thread to begin execution; the VM calls the run method of
	 * this thread. The result is that two threads are running concurrently: the
	 * current thread (which returns from the call to the start method) and the
	 * other thread (which executes its run method).
	 *
	 * <p>
	 * It is never legal to start a thread more than once. In particular, a
	 * thread may not be restarted once it has completed execution.
	 */
	public final native synchronized void start();

	/**
	 * Returns the thread priority.
	 *
	 * @return the thread priority.
	 */
	public final int getPriority() {
		return priority;
	}

	/**
	 * <p>
	 * Set the thread priority. Thread priorities must range between
	 * {@link #MIN_PRIORITY} and {@link #MAX_PRIORITY} (inclusive).
	 *
	 * <p>
	 * Calling code must have the "modifyThread" {@link RuntimePermission}.
	 *
	 * @param newPriority
	 */
	public final void setPriority(int newPriority) {
		SecurityManager.checkPermission(SecurityManager.MODIFY_THREAD_PERMISSION);
		if ((newPriority > MAX_PRIORITY) || (newPriority < MIN_PRIORITY))
			throw new IllegalArgumentException("Invalid Thread Priority");

		setPriority0(newPriority);
	}

	/*
	 * made the set priority a native function so it can recalc the timeslice of
	 * the VM thread it belongs to now rather than each interpretor loop
	 */
	private native void setPriority0(int newPriority);

	/**
	 * Returns the thread name.
	 *
	 * @return the thread name.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * <p>
	 * Set the name of a thread to a given name.
	 *
	 * <p>
	 * Calling code must have the "modifyThread" {@link RuntimePermission}.
	 *
	 * @param newName
	 */
	public final void setName(String newName) {
		SecurityManager.checkPermission(SecurityManager.MODIFY_THREAD_PERMISSION);
		name = newName;
	}

	/**
	 * <p>
	 * Returns the currently executing thread.
	 *
	 * <p>
	 * Calling code must have the "getThread" {@link RuntimePermission}.
	 *
	 * @return the currently executing thread.
	 */
	public final static Thread currentThread() {
		SecurityManager.checkPermission(SecurityManager.GET_THREAD_PERMISSION);
		return currentThread0();
	}

	private static native Thread currentThread0();

	/**
	 * Tests if this thread is alive. A thread is alive if it has been started
	 * and has not yet died.
	 *
	 * @return <code>true</code> is the thread is alive,
	 *         <code>false</code> otherwise.
	 */
	public final native boolean isAlive();

	/**
	 * Tests whether this thread has been interrupted. The interrupted status of
	 * the thread is unaffected by this method.
	 *
	 * @return <code>true</code> is the thread is interrupted,
	 *         <code>false</code> otherwise.
	 */
	public final boolean isInterrupted() {
		return isInterrupted(false);
	}

	/**
	 * Tests whether the current thread has been interrupted. The interrupted
	 * status of the thread is cleared by this method. In other words, if this
	 * method were to be called twice in succession, the second call would
	 * return false (unless the current thread were interrupted again, after the
	 * first call had cleared its interrupted status and before the second call
	 * had examined it).
	 *
	 * <p>
	 * A thread interruption ignored because a thread was not alive at the time
	 * of the interrupt will be reflected by this method returning false.
	 *
	 * @return <code>true</code> if the current thread has been interrupted;
	 *         <code>false</code> otherwise.
	 */
	public final static boolean interrupted() {
		return currentThread0().isInterrupted(true);
	}

	private native boolean isInterrupted(boolean clearInterrupted);

	/**
	 * Interrupts this thread.
	 *
	 * <p>
	 * Calling code must have the "modifyThread" {@link RuntimePermission}.
	 * Unlike J2SE which always allows a thread to interrupt itself (which no
	 * security check), we always perform a security check here.
	 */
	public final void interrupt() {
		SecurityManager.checkPermission(SecurityManager.MODIFY_THREAD_PERMISSION);
		interrupt0();
	}

	private native void interrupt0();

	/**
	 * Waits at most <code>millis</code> milliseconds for this thread to die. A
	 * timeout of 0 means to wait forever.
	 *
	 * @param millis the time to wait in milliseconds.
	 * @throws InterruptedException if any thread has interrupted the current
	 *             thread. The interrupted status of the current thread is
	 *             cleared when this exception is thrown.
	 */
	public synchronized final void join(long millis) throws InterruptedException {
		long base = System.currentTimeMillis();
		long now = 0;

		if (millis < 0) {
			throw new IllegalArgumentException("timeout negative");
		}

		if (millis == 0) {
			while (isAlive()) {
				wait(0);
			}
		} else {
			while (isAlive()) {
				long delay = millis - now;
				if (delay <= 0) {
					break;
				}
				wait(delay);
				now = System.currentTimeMillis() - base;
			}
		}
	}

	/**
	 * Waits for this thread to die.
	 *
	 * @throws InterruptedException if any thread has interrupted the current
	 *             thread. The interrupted status of the current thread is
	 *             cleared when this exception is thrown.
	 */
	public synchronized final void join() throws InterruptedException {
		while (isAlive()) {
			wait(0);
		}
	}

	/**
	 * Causes the currently executing thread object to temporarily pause and
	 * allow other threads to execute.
	 */
	public final native static void yield();

	/**
	 * Causes the currently executing thread to sleep (temporarily cease
	 * execution) for the specified number of milliseconds, subject to the
	 * precision and accuracy of system timers and schedulers.
	 *
	 * <p>
	 * The thread does not lose ownership of any monitors.
	 *
	 * @param millis the minimum number of milliseconds to sleep for.
	 * @throws InterruptedException if any thread has interrupted the current
	 *             thread. The interrupted status of the current thread is
	 *             cleared when this exception is thrown.
	 */
	public final native static void sleep(long millis) throws InterruptedException;

	/**
	 * Returns the state of this thread. This method is designed for use in
	 * monitoring of the system state, not for synchronization control.
	 *
	 * @return the thread state.
	 */
	public final native int getState();

	/**
	 * <p>
	 * Sets whether a thread is a daemon thread. The daemon status of a thread
	 * cannot be set while it is alive.
	 *
	 * <p>
	 * Calling code must have the "modifyThread" {@link RuntimePermission}.
	 *
	 * @param on
	 */
	public final void setDaemon(boolean on) {
		SecurityManager.checkPermission(SecurityManager.MODIFY_THREAD_PERMISSION);
		if (isAlive()) {
			throw new IllegalThreadStateException();
		}
		daemon = on;
	}

	/**
	 * Report if this thread is a daemon thread.
	 *
	 * @return <code>true</code> if this thread is a daemon thread,
	 *         <code>false</code> otherwise.
	 */
	public final boolean isDaemon() {
		return daemon;
	}

	/**
	 * Return the ID of this thread.
	 *
	 * @return the ID.
	 */
	public final int getId() {
		return id;
	}

	/**
	 * An interface for a handler of uncaught exceptions.
	 *
	 * @author Greg McCreath
	 *
	 */
	public interface UncaughtExceptionHandler {

		/**
		 * Handle an uncaught exception. Any uncaught exceptions thrown from an
		 * implementation of this method are ignored.
		 *
		 * @param t the thread the uncaught exception occurred in.
		 * @param e the uncaught exception.
		 */
		void uncaughtException(Thread t, Throwable e);
	}

	/**
	 * <p>
	 * Sets the default uncaught exception handler for all threads.
	 *
	 * <p>
	 * Calling code must have the "setDefaultUncaughtExceptionHandler"
	 * {@link RuntimePermission}.
	 *
	 * @param eh the new default UncaughtExceptionHandler.
	 */
	public final static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
		SecurityManager.checkPermission(new RuntimePermission("setDefaultUncaughtExceptionHandler"));
		_defaultUncaughtExceptionHandler = eh;
	}

	/**
	 * Returns the default uncaught exception handler.
	 *
	 * @return the default uncaught exception handler.
	 */
	public final static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
		return _defaultUncaughtExceptionHandler;
	}

	/**
	 * Returns the uncaught exception handler for this thread.
	 *
	 * @return the uncaught exception handler for this thread.
	 */
	public final UncaughtExceptionHandler getUncaughtExceptionHandler() {
		return _uncaughtExceptionHandler != null ? _uncaughtExceptionHandler : _defaultUncaughtExceptionHandler;
	}

	/**
	 * <p>
	 * Sets the uncaught exception handler for this thread.
	 *
	 * <p>
	 * Calling code must have the "modifyThread" {@link RuntimePermission}.
	 *
	 * @param eh the new UncaughtExceptionHandler.
	 */
	public final void setUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
		SecurityManager.checkPermission(SecurityManager.MODIFY_THREAD_PERMISSION);
		_uncaughtExceptionHandler = eh;
	}

	/**
	 * Called by the VM when an uncaught exception is encountered - don't mess
	 * with it.
	 *
	 * Call the UncaughtExceptionHandler for the current thread and ignores any
	 * exceptions thrown by it.
	 *
	 * If a thread has no uncaught exception handler, an error message is
	 * printed to {@link System#err} as well as a stack trace of the uncaught
	 * exception.
	 *
	 * @param e the uncaught exception.
	 */
	private void dispatchUncaughtException(Throwable e) {
		// call the uncaught exception handler. Eat all subsequent exceptions.
		try {
			UncaughtExceptionHandler handler = getUncaughtExceptionHandler();
			if (handler != null) {
				handler.uncaughtException(this, e);
			} else {
				System.err.print("Exception in thread \"" + this.getName() + "\" ");
				e.printStackTrace(System.err);
			}
		} catch (Throwable t) {
		}
	}

	/**
	 * Print a stacktrace for the current place in the thread.
	 */
	public static void dumpStack() {
		new Exception("Stack trace").printStackTrace();
	}

}

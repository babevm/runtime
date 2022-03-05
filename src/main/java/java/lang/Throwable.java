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

import java.io.PrintStream;

/*
 *
 * Notes: There are no constructors with 'cause' in it. The older 'initCause' is
 * used instead - it saves two constructors on every exception - which adds up
 * to a lot of space savings.
 *
 * The constructor is not called by the VM if the exception is native.
 */

/**
 * @author Greg McCreath
 * @since 0.0.1
 */
public class Throwable {

	private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];

	/* do not move or rearrange these fields - they are used by the VM */
	/* ********************************************** */
	private String _message;
	private Throwable _cause;

	/*
	 * handle used by VM to store stack information if a printStackTrace() is
	 * executed
	 */
	private Object _backtrace;

	/*
	 * flag used by the VM to track the native state of the exception.
	 */
	private boolean _needs_native_try_reset;

	/* */
	private StackTraceElement[] _stackTraceElements;

	/* ********************************************** */

	public Throwable() {
		fillInStackTrace();
	}

	public Throwable(String message) {
		fillInStackTrace();
		_message = message;
	}

	public String getMessage() {
		return _message;
	}

	public String toString() {
		String s = getClass().getName();
		return (_message != null) ? (s + ": " + _message) : s;
	}

	public Throwable getCause() {
		return _cause;
	}

	public Throwable initCause(Throwable cause) {

		if (_cause != null)
			throw new IllegalArgumentException("Cannot overwrite cause");

		if (cause == this)
			throw new IllegalArgumentException("Self-causation not permitted");

		_cause = cause;
		return this;
	}

	public void printStackTrace() {
		printStackTrace(System.out);
	}

	public void printStackTrace(PrintStream s) {
		synchronized (s) {
			s.println(this);
			StackTraceElement[] trace = getStackTrace1();
			for (int i = 0; i < trace.length; i++)
				s.println("\tat " + trace[i]);

			if (_cause != null)
				_cause.printStackTraceAsCause(s, trace);
		}
	}

	/**
	 * Print our stack trace as a cause for the specified stack trace.
	 */
	private void printStackTraceAsCause(PrintStream s, StackTraceElement[] causedTrace) {

		// Compute number of frames in common between this and caused
		StackTraceElement[] trace = getStackTrace1();
		int m = trace.length - 1, n = causedTrace.length - 1;
		while (m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
			m--;
			n--;
		}
		int framesInCommon = trace.length - 1 - m;

		s.println("Caused by: " + this);
		for (int i = 0; i <= m; i++)
			s.println("\tat " + trace[i]);
		if (framesInCommon != 0)
			s.println("\t... " + framesInCommon + " more");

		// Recurse if we have a cause
		Throwable ourCause = getCause();
		if (ourCause != null)
			ourCause.printStackTraceAsCause(s, trace);
	}

	public StackTraceElement[] getStackTrace() {
		return (StackTraceElement[]) getStackTrace1().clone();
	}

	private StackTraceElement[] getStackTrace1() {

		if (_stackTraceElements != null)
			return _stackTraceElements;

		/* if null, ask the native layer to get the stack trace. */
		_stackTraceElements = getStackTrace0();

		/*
		 * if still null - which may be the case for some VM errors, like out of
		 * memory error, use an empty one
		 */
		if (_stackTraceElements == null)
			_stackTraceElements = EMPTY_STACK_TRACE;

		return _stackTraceElements;
	}

	private native StackTraceElement[] getStackTrace0();

	public synchronized native void fillInStackTrace();

}

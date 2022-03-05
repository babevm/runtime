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

/**
 * <code>RuntimeException</code> is the superclass of those exceptions that can
 * be thrown during the normal operation of the Virtual Machine.
 *
 * A method is not required to declare in its throws clause any subclasses of
 * <code>RuntimeException</code> that might be thrown during the execution of
 * the method but not caught.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class RuntimeException extends Exception {

	public RuntimeException() {
		super();
	}

	public RuntimeException(String message) {
		super(message);
	}

	/**
	 * Override of {@link Throwable#initCause(Throwable)} to return a
	 * RuntimeException and not a throwable.
	 *
	 * This allows usage like:
	 *
	 * <pre>
	 * catch (AnyThrowableClass e) {
	 *    throw new RuntimeException("message").initCause(e);
	 * }
	 * </pre>
	 *
	 * from methods that do not actually define a checked exception.
	 *
	 *
	 * @see java.lang.Throwable#initCause(java.lang.Throwable)
	 */
	public RuntimeException initCause(Throwable cause) {
		return (RuntimeException) super.initCause(cause);
	}

}

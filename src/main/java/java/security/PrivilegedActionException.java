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
 *
 * This exception is thrown by
 * {@link SecurityManager#doPrivileged(PrivilegedExceptionAction)} to indicate
 * that the action being performed threw a checked exception. The exception that
 * caused this exception can be gained using {@link #getCause()}.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class PrivilegedActionException extends Exception {

	/**
	 * Creates a new PrivilegedActionException for a given cause exception.
	 *
	 * @param exception a given exception.
	 */
	public PrivilegedActionException(Exception exception) {
		super();
		this.initCause(exception);
	}

}

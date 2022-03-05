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
 * <p>A computation to be performed with privileges enabled, that throws one or
 * more checked exceptions. The computation is performed by invoking
 * {@link SecurityManager#doPrivileged(PrivilegedAction)} on the
 * PrivilegedExceptionAction object.
 *
 * <p>This interface is used only for computations that throw checked exceptions;
 * computations that do not throw checked exceptions should use
 * {@link PrivilegedAction} instead.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 * @param <T> the class type of the return value from the privileged code.
 */
public interface PrivilegedExceptionAction<T> {

	/**
	 * Performs the computation. This method will be called by
	 * {@link SecurityManager#doPrivileged(PrivilegedAction)}.
	 *
	 * @return a generic value specified by T.
	 * @throws Exception if any exception occurs.
	 */
	T run() throws Exception;
}

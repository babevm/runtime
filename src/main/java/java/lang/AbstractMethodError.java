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
 * Thrown when an application tries to call an abstract method. Normally, this
 * error is caught by the compiler; this error can only occur at run time if the
 * definition of some class has incompatibly changed since the currently
 * executing method was last compiled.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class AbstractMethodError extends IncompatibleClassChangeError {

	public AbstractMethodError() {
		super();
	}

	public AbstractMethodError(String message) {
		super(message);
	}

}

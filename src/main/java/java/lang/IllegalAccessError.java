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
 * Thrown if an application attempts to access or modify a field, or to call a
 * method that it does not have access to.
 *
 * Normally, this error is caught by the compiler; this error can only occur at
 * run time if the definition of a class has incompatibly changed.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class IllegalAccessError extends IncompatibleClassChangeError {

	public IllegalAccessError() {
		super();
	}

	public IllegalAccessError(String message) {
		super(message);
	}

}

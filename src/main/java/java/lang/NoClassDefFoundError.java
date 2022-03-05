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
 * Thrown if the Java Virtual Machine or a ClassLoader instance tries to load in
 * the definition of a class (as part of a normal method call or as part of
 * creating a new instance using the new expression) and no definition of the
 * class could be found.
 *
 * The searched-for class definition existed when the currently executing class
 * was compiled, but the definition can no longer be found.
 *
 * @author Greg McCreath
 * @since 0.0.1
 *
 */
public class NoClassDefFoundError extends LinkageError {

	public NoClassDefFoundError() {
		super();
	}

	public NoClassDefFoundError(String message) {
		super(message);
	}

}

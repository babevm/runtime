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

import java.security.SecurityManager;
import java.security.RuntimePermission;

/**
 * Every Java application has a single instance of class Runtime that allows the
 * application to interface with the environment in which the application is
 * running. The current runtime can be obtained from the getRuntime method.
 *
 * An application cannot create its own instance of this class.
 *
 * @author Greg McCreath
 * @since 0.0.1
 */

public final class Runtime {

	private Runtime() {
	};

	private static Runtime _instance = new Runtime();

	public static Runtime getRuntime() {
		return _instance;
	}

	public native long freeMemory();

	public native long totalMemory();

	/**
	 * Abruptly exist the VM.
	 *
	 * <p>
	 * Calling code must have "exitVM.x" {@link RuntimePermission} where 'x' is
	 * the given exit code or '*'.
	 *
	 * @param code
	 */
	public void exit(int code) {
		SecurityManager.checkPermission(new RuntimePermission("exitVM." + code));
		exit0(code);
	}

	private native void exit0(int code);

	public native void gc();

}

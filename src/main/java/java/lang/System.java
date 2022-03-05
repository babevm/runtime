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

import java.io.Console;
import java.io.PrintStream;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.SecurityManager;
import java.util.Hashtable;
import java.util.PropertyPermission;

/**
 * @author Greg McCreath
 * @since 0.0.1
 */
public final class System {

	/** fields are explicitly used by the VM ... DO NOT move or alter or rearrange * */
	/** the _cmdline field is set by the VM to contain all the system properties set
	 * on the command line using the -Dxxx format.  It is set on VM startup and processed
	 * by {@link System#parseSystemProperties(String[])}} as this class is initialised. */
	private static String[] _cmdLine;
	/***/

	private static Hashtable<String, String> _properties = new Hashtable<String, String>();

	public static PrintStream out = Console.getInstance();

	public static PrintStream err = Console.getInstance();

	static {

		if (_cmdLine != null) parseSystemProperties(_cmdLine);

	}

	private System() {};

	public static native void arraycopy(Object src, int srcPos, Object dest, int destPos, int length);

	public static void exit(int code) {
		Runtime.getRuntime().exit(code);
	}

	public static native long currentTimeMillis();

	public static native int identityHashCode(Object x);

	public static void gc() {
		Runtime.getRuntime().gc();
	}

	/* used by the VM to place dummy frames on the stack during execution */
	private static void noop() {};

	/* used by the VM to place dummy frames on the stack during execution */
	private static void noop_ret() {};

	/* used by the VM to have callbacks into the VM placed in the stack. */
	private static void callbackwedge(Object reference, Object callback, Object data) {};

	public static long nanoTime() {
		return currentTimeMillis();
	}

	public static synchronized String setProperty(String key, String value) {

		if ( (key == null) || (value == null))
			throw new NullPointerException();

		if (key == "")
			throw new IllegalArgumentException();

		SecurityManager.checkPermission(new PropertyPermission(key, Permission.ACTION_WRITE));

		return _properties.put(key, value);
	}

	public static String getProperty(String key) {

		if (key == null)
			throw new NullPointerException();

		if (key == "")
			throw new IllegalArgumentException();

		SecurityManager.checkPermission(new PropertyPermission(key, Permission.ACTION_READ));

		return _properties.get(key);
	}

	private static void parseSystemProperties(String[] args) {

		SecurityManager.doPrivileged( new PrivilegedAction<Object> () {

			public Object run() {

				for (int lc=0; lc < _cmdLine.length; lc++) {
					String arg = _cmdLine[lc];
					int pos = arg.indexOf('=');
					String key = arg.substring(0, pos);
					String value = arg.substring(pos+1, arg._length).replace('"', ' ').trim();
					setProperty(key, value);
				}

				return null;
			}

		});
	}
}

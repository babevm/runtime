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

package java.io;

/**
 * @author Greg McCreath
 * @since 0.0.1
 */
public class Console extends OutputStream implements PrintStream {

	private static Console _instance;

    public native void write(int b) throws IOException;

    // hide constructor for singleton
    private Console() {
    }

    public static Console getInstance() {
    	if (_instance == null) _instance = new Console();
    	return _instance;
    }

	public void println() {
		println("");
	};

	public native void println0(String s);

	public void println(String s) {
		String str = (s == null) ? "null" : s;
		println0(str);
	}

	public native void print0(String s);

	public void print(String s) {
		String str = (s == null) ? "null" : s;
		print0(str);
	}

	public void println(Object o) {
		println(o.toString());
	}

	public void println(int i) {
		println(String.valueOf(i));
	}

	public void println(char c) {
		println(String.valueOf(c));
	}

	public void println(boolean b) {
		println(String.valueOf(b));
	}

	public void println(float f) {
		println(String.valueOf(f));
	}

	public void println(long l) {
		println(String.valueOf(l));
	}

	public void print(Object o ) {
		String s = (o == null) ? "null" : o.toString();
		print(s);
	}

	public void print(int i) {
		print(String.valueOf(i));
	}

	public void print(char c) {
		print(String.valueOf(c));
	}

	public void print(boolean b) {
		print(String.valueOf(b));
	}

	public void print(float f) {
		print(String.valueOf(f));
	}

	public void print(long l) {
		print(String.valueOf(l));
	}
}

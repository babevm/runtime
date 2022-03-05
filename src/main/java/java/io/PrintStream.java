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
public interface PrintStream {

	void println();

	void println(Object s);

	void println(String s);

	void println(int i);

	void println(char c);

	void println(boolean b);

	void println(float f);

	void println(long l);

	void print(Object s);

	void print(String s);

	void print(int i);

	void print(char c);

	void print(boolean b);

	void print(float f);

	void print(long l);
}

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
 * @author Greg McCreath
 * @since 0.0.1
 */
public class Object {

	public native int hashCode();

	public String toString() {
		return getClass().getName() + '@' + hashCode();
	}

	public boolean equals(Object o) {
		return ( this == o );
	}

	public native Class<?> getClass();

	public final void wait() throws InterruptedException {
		this.wait(0);
	}

	public native void wait(long millis) throws InterruptedException;

	public native final void notify();

	public native final void notifyAll();

    protected native Object clone() throws CloneNotSupportedException;
}
